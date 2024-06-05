package com.meteor.chat.user.service.impl;

import cn.hutool.core.lang.Pair;
import com.meteor.chat.common.domain.dto.ItemInfoDTO;
import com.meteor.chat.common.domain.dto.SummaryInfoDTO;
import com.meteor.chat.common.domain.entity.IpInfo;
import com.meteor.chat.common.domain.entity.ItemConfig;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.entity.UserBackpack;
import com.meteor.chat.common.domain.enums.ChatActiveStatusEnum;
import com.meteor.chat.common.domain.enums.ItemConfigTypeEnum;
import com.meteor.chat.common.domain.enums.RoleEnum;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.UserInfoVO;
import com.meteor.chat.common.domain.vo.req.*;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.common.util.CommonUtils;
import com.meteor.chat.event.BlackUserEvent;
import com.meteor.chat.event.UserRegisterEvent;
import com.meteor.chat.user.dao.BlackDao;
import com.meteor.chat.user.dao.UserBackpackDao;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.dao.UserRoleDao;
import com.meteor.chat.user.service.UserBackpackService;
import com.meteor.chat.user.service.UserService;
import com.meteor.chat.user.service.adapter.UserAdapter;
import com.meteor.chat.user.service.cache.ItemCache;
import com.meteor.chat.user.service.cache.UserCache;
import com.meteor.chat.user.service.cache.UserSummaryCache;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private UserCache userCache;
    @Resource
    private UserBackpackService userBackpackService;
    @Resource
    private UserBackpackDao userBackpackDao;
    @Resource
    private UserRoleDao userRoleDao;
    @Resource
    private BlackDao blackDao;
    @Resource
    private ItemCache itemCache;
    @Resource
    private UserSummaryCache userSummaryCache;

    @Override
    public void register(User user) {
        userDao.save(user);
        //用户注册事件推送
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, user));
    }

    @Override
    public UserInfoVO getUserInfo(Long uid) {
        User userInfo = userCache.getUserInfo(uid);
        // 背包改名卡查询，获取用户可改名次数
        int renameTimes = userBackpackService.countRenameTimes(uid);
        return UserAdapter.buildUserInfoResp(userInfo, renameTimes);
    }

    @Override
    public void wearBadge(Long uid, Long itemId) {
        List<UserBackpack> userBackpacks = userBackpackDao.listByUidAndItem(uid, itemId);
        if (CollectionUtils.isEmpty(userBackpacks)) {
            throw new BusinessException("暂时拥有该徽章");
        }
        ItemConfig item = itemCache.getById(itemId);
        Assert.assertEquals("该徽章无法佩戴", ItemConfigTypeEnum.BADGE.getType(), item.getType());
        userDao.wearBadge(uid, itemId);
        userCache.userInfoChange(uid);
    }

    @Override
    public boolean isAdmin(Long uid) {
        return userRoleDao.hasPower(uid, RoleEnum.SUPERADMIN.getId())
                || userRoleDao.hasPower(uid, RoleEnum.CHAT_ADMIN.getId());
    }

    @Override
    public void black(Long blackId) {
        User blackUser = userCache.getUserInfo(blackId);
        if (blackUser == null) {
            throw new BusinessException("拉黑的用户不存在");
        }
        blackDao.blackUid(blackUser.getId());
        IpInfo ipInfo = blackUser.getIpInfo();
        blackDao.blackIP(ipInfo.getCreateIp());
        // 如果ip相同不重复拉黑，以免数据库报错
        if (!ipInfo.getCreateIp().equals(ipInfo.getUpdateIp())) {
            blackDao.blackIP(ipInfo.getUpdateIp());
        }
        applicationEventPublisher.publishEvent(new BlackUserEvent(this, blackUser));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rename(Long uid, ModifyNameReq req) {
        String name = req.getName();
        // todo 判断名称中是否包含敏感词
        // 判断名称是否重复，需要保证名称不重复
        List<User> userByName = userDao.getByName(name);
        Assert.assertTrue("该昵称已被占用，请选择其他昵称", CollectionUtils.isEmpty(userByName));
        UserBackpack renameCard = userBackpackService.getOneBackpackByItemType(uid, ItemConfigTypeEnum.MODIFY_NAME_CARD.getType());
        Assert.assertNotNull("改名卡数量不足，请前往背包购买", renameCard);
        // 使用乐观锁的方式进行使用改名卡和修改用户名称
        boolean success = userBackpackService.useBackpackItem(renameCard.getId());
        if (success) {
            userDao.rename(uid, name);
            userCache.userInfoChange(uid);
        }
    }

    @Override
    public List<SummaryInfoDTO> getSummaryInfoDTOList(SummaryInfoReq req) {
        List<SummaryInfoReq.infoReq> reqList = req.getReqList();
        // 先获取到需要更新的用户id列表
        List<Long> needRefreshUidList = getNeedRefreshUid(reqList);
        // 批量获取需要更新的用户信息
        Map<Long, SummaryInfoDTO> summaryInfoDTOMap = userSummaryCache.getBatch(needRefreshUidList);
        return reqList.stream().map(infoReq -> {
            if (summaryInfoDTOMap.containsKey(infoReq.getUid())) {
                return summaryInfoDTOMap.get(infoReq.getUid());
            } else {
                return SummaryInfoDTO.skip(infoReq.getUid());
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<ItemInfoDTO> getItemInfoDTOList(ItemInfoReq req) {
        return req.getReqList().stream().map(infoReq -> {
            ItemConfig item = itemCache.getById(infoReq.getItemId());
            // 如果请求没有最后更新时间 或者 请求中的最后更新时间小于数据库中徽章的更新时间，那么说明需要更新
            // 封装更新的数据
            if (Objects.isNull(infoReq.getLastModifyTime()) || (Objects.nonNull(item.getUpdateTime()) && item.getUpdateTime().getTime() > infoReq.getLastModifyTime())) {
                ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
                itemInfoDTO.setItemId(item.getId());
                itemInfoDTO.setNeedRefresh(Boolean.TRUE);
                itemInfoDTO.setImg(item.getImg());
                itemInfoDTO.setDescribe(item.getDescribe());
                return itemInfoDTO;
            } else {
                return ItemInfoDTO.skip(infoReq.getItemId());
            }
        }).collect(Collectors.toList());
    }

    @Override
    public CursorPageBaseResp<User> cursorPageUser(MemberCursorReq req, List<Long> uidList) {
        // 获取请求中的游标信息，获取成员的游标形式为  在线/离线_游标（最后上下线时间）
        Pair<ChatActiveStatusEnum, String> cursorPair = CommonUtils.getMemberCursor(req.getCursor());
        String timeCursor = cursorPair.getValue();
        // 游标分页获取数据
        CursorPageBaseResp<User> userPage = userDao.cursorPage(new CursorPageBaseReq(req.getPageSize(), timeCursor), cursorPair.getKey(), uidList);
        List<User> data = userPage.getData();
        boolean isLast = userPage.getIsLast();
        String cursor = CommonUtils.generateMemberCursor(cursorPair.getKey(), userPage.getCursor());
        if (cursorPair.getKey() == ChatActiveStatusEnum.ONLINE && userPage.getData().size() < req.getPageSize()) {
            // 如果是获取在线的分页，且数量不足，需要补充离线的用户数据
            // 先计算需要补充多少条记录
            int count = req.getPageSize() - userPage.getData().size();
            // 获取离线的补充数据
            CursorPageBaseResp<User> offlinePage = userDao.cursorPage(new CursorPageBaseReq(count, null), ChatActiveStatusEnum.OFFLINE, uidList);
            data.addAll(offlinePage.getData());
            // 根据离线的分页数据，重置分页的属性：是否最后一页，游标信息
            isLast = offlinePage.getIsLast();
            cursor = CommonUtils.generateMemberCursor(ChatActiveStatusEnum.OFFLINE, offlinePage.getCursor());
        }
        return new CursorPageBaseResp<>(cursor, isLast, data);
    }

    private List<Long> getNeedRefreshUid(List<SummaryInfoReq.infoReq> reqList) {
        ArrayList<Long> needRefreshUids = new ArrayList<>();
        List<Long> modifyTime = userCache.getUserModifyTime(reqList.stream().map(SummaryInfoReq.infoReq::getUid).collect(Collectors.toList()));
        for (int i = 0; i < reqList.size(); i++) {
            SummaryInfoReq.infoReq infoReq = reqList.get(i);
            Long time = modifyTime.get(i);
            // 如果请求没有最后更新时间 或者 请求中的最后更新时间小于数据库中用户的更新时间，那么说明需要更新
            if (Objects.isNull(infoReq.getLastModifyTime()) || (Objects.nonNull(time) && infoReq.getLastModifyTime() < time)) {
                needRefreshUids.add(infoReq.getUid());
            }
        }
        return needRefreshUids;
    }
}
