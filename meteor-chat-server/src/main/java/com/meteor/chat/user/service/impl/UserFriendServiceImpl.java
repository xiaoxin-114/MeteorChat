package com.meteor.chat.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.entity.UserApply;
import com.meteor.chat.common.domain.entity.UserFriend;
import com.meteor.chat.common.domain.enums.UserApplyStatusEnum;
import com.meteor.chat.common.domain.vo.*;
import com.meteor.chat.common.domain.vo.req.*;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.event.NewFriendEvent;
import com.meteor.chat.event.NewUserApplyEvent;
import com.meteor.chat.chat.dao.RoomFriendDao;
import com.meteor.chat.chat.service.RoomService;
import com.meteor.chat.chat.service.adapter.RoomAdapter;
import com.meteor.chat.user.dao.UserApplyDao;
import com.meteor.chat.user.dao.UserFriendDao;
import com.meteor.chat.user.service.UserFriendService;
import com.meteor.chat.user.service.adapter.FriendAdapter;
import com.meteor.chat.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserFriendServiceImpl implements UserFriendService {

    @Resource
    private UserFriendDao userFriendDao;

    @Resource
    private UserCache userCache;

    @Resource
    private UserApplyDao userApplyDao;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private RoomService roomService;

    @Resource
    private RoomFriendDao roomFriendDao;

    @Override
    public CursorPageBaseResp<FriendResp> pageFriendList(Long uid, CursorPageBaseReq request) {
        CursorPageBaseResp<UserFriend> userFriendList = userFriendDao.pageFriendList(uid, request);
        List<UserFriend> userFriends = userFriendList.getData();
        // 转换成前端需要的格式
        if (CollectionUtils.isEmpty(userFriends)) {
            return null;
        }
        List<Long> friendIds = userFriends.stream().map(UserFriend::getFriendUid).collect(Collectors.toList());
        Map<Long, User> users = userCache.getUserInfoBatch(friendIds);
        List<FriendResp> friendResps = FriendAdapter.convertFriendRespList(friendIds, users);
        return CursorPageBaseResp.init(userFriendList, friendResps);
    }

    @Override
    public void applyUser(Long uid, Long targetUid, String msg) {
        UserFriend friend = userFriendDao.getFriend(uid, targetUid);
        Assert.assertNull("你们已经是好友了", friend);
        UserApply userApply = userApplyDao.getWaitingApply(uid, targetUid);
        Assert.assertNull("已经提交过申请了", userApply);
        // 查询对方是否发起过好友申请，如果发起过就直接同意申请，不再发送
        UserApply converseApply = userApplyDao.getWaitingApply(targetUid, uid);
        if (converseApply != null) {
            ((UserFriendService) AopContext.currentProxy()).processApply(new FriendApproveReq(converseApply.getId()));
        } else {
            UserApply insert = FriendAdapter.buildNewApply(uid, targetUid, msg);
            userApplyDao.save(insert);
            applicationEventPublisher.publishEvent(new NewUserApplyEvent(this, insert));
        }
    }

    @Override
    public FriendUnreadResp countUnread(Long uid) {
        return new FriendUnreadResp(userApplyDao.countUnRead(uid));
    }

    @Override
    public FriendCheckResp batchCheckFriends(Long uid, FriendCheckReq req) {
        List<UserFriend> friends = userFriendDao.getFriends(uid);
        List<Long> friendIdList = Optional.ofNullable(friends)
                .map(list -> list.stream().map(UserFriend::getFriendUid).collect(Collectors.toList()))
                .orElse(new ArrayList<Long>());
        List<Long> targetIds = req.getUidList();
        List<FriendCheckResp.FriendCheck> friendCheckList = targetIds.stream().map(id -> {
            boolean isFriend = friendIdList.contains(id);
            FriendCheckResp.FriendCheck check = new FriendCheckResp.FriendCheck();
            check.setUid(id);
            check.setIsFriend(isFriend);
            return check;
        }).collect(Collectors.toList());
        return new FriendCheckResp(friendCheckList);
    }

    @Override
    public PageBaseResp<FriendApplyResp> applyPage(PageBaseReq req, Long uid) {
        Page<UserApply> applyPage = userApplyDao.getApplyPage(req.plusPage(), uid);
        if (CollectionUtils.isEmpty(applyPage.getRecords())) {
            return PageBaseResp.empty();
        }
        // 把这些好友申请都设置为已读
        readApply(applyPage.getRecords());
        return PageBaseResp.init(applyPage,
                FriendAdapter.convertToFriendApplyList(applyPage.getRecords()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processApply(FriendApproveReq req) {
        UserApply userApply = userApplyDao.getById(req.getApplyId());
        Assert.assertNotNull("无好友申请记录", userApply);
        Assert.assertEquals("好友申请状态错误", UserApplyStatusEnum.WAITING.getCode(), userApply.getStatus());
        // 修改好友申请的处理状态
        userApplyDao.approveApply(req.getApplyId());
        // 添加两者的好友关系
        addFriend(userApply.getUid(), userApply.getTargetId());
        // 创建单聊会话，这里添加好友和创建单聊会话不能通过时间监听机制，因为要保证事务统一性
        Long roomId = roomService.buildSingleRoom(userApply.getUid(), userApply.getTargetId());
        applicationEventPublisher.publishEvent(new NewFriendEvent(this, userApply, roomId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(FriendDeleteReq req, Long uid) {
        List<UserFriend> friendRelation = userFriendDao.getFriendRelation(req.getFriendId(), uid);
        if (CollectionUtils.isEmpty(friendRelation)) {
            throw new BusinessException( req.getFriendId() + "和" + uid + "没有好友关系");
        }
        // 删除好友关系
        userFriendDao.removeByIds(
                friendRelation.stream()
                        .map(UserFriend::getId).collect(Collectors.toList()));
        // 禁用单聊聊天室
        roomFriendDao.disableRoomByKey(RoomAdapter.buildRoomKey(req.getFriendId(), uid));
    }

    private void addFriend(Long uid, Long targetId) {
        UserFriend userFriend01 = FriendAdapter.buildUserFriend(uid, targetId);
        UserFriend userFriend02 = FriendAdapter.buildUserFriend(targetId, uid);
        userFriendDao.saveBatch(Arrays.asList(userFriend01, userFriend02));
    }

    private void readApply(List<UserApply> records) {
        userApplyDao.readApply(records.stream().map(UserApply::getId).collect(Collectors.toList()));
    }
}
