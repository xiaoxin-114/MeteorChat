package com.meteor.chat.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meteor.chat.common.annotation.RedissonLock;
import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.domain.dto.SummaryInfoDTO;
import com.meteor.chat.common.domain.entity.ItemConfig;
import com.meteor.chat.common.domain.entity.UserBackpack;
import com.meteor.chat.common.domain.enums.ItemConfigTypeEnum;
import com.meteor.chat.common.domain.vo.BadgeResp;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.event.ItemReceiveEvent;
import com.meteor.chat.user.dao.UserBackpackDao;
import com.meteor.chat.user.service.UserBackpackService;
import com.meteor.chat.user.service.adapter.UserAdapter;
import com.meteor.chat.user.service.cache.ItemCache;
import com.meteor.chat.user.service.cache.UserSummaryCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class UserBackpackServiceImpl implements UserBackpackService {

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private UserBackpackDao userBackpackDao;
    @Resource
    private ItemCache itemCache;
    @Resource
    private UserSummaryCache userSummaryCache;


    @Override
    @RedissonLock(key = "#idempotent", time = 5000)
    public void issueItem(Long uid, Long itemId, String idempotent) {
        // 幂等判断
        UserBackpack backpack = userBackpackDao.getByIdempotent(idempotent);
        if (Objects.nonNull(backpack)) {
            return;
        }
        ItemConfig item = itemCache.getById(itemId);
        // 如果是徽章，用户背包已经存在的话就不发了
        if (ItemConfigTypeEnum.BADGE.getType().equals(item.getType())) {
            int count = userBackpackDao.getCountByUidAndItemId(uid, itemId);
            if (count > 0) {
                return;
            }
        }
        UserBackpack userbackpack = UserBackpack.builder()
                .itemId(itemId)
                .uid(uid)
                .idempotent(idempotent)
                .createTime(new Date())
                .status(CommonConstants.USER_BACK_PACK_NOT_USED).build();
        userBackpackDao.save(userbackpack);
        // 发布用户收到事件
        applicationEventPublisher.publishEvent(new ItemReceiveEvent(this, userbackpack));
    }

    @Override
    public List<UserBackpack> listByUidAndItemId(List<Long> list, List<Long> itemIdList) {
        return userBackpackDao.listByUidsAndItems(list, itemIdList);
    }

    @Override
    public int countRenameTimes(Long uid) {
        List<ItemConfig> modifyCard = itemCache.getByType(ItemConfigTypeEnum.MODIFY_NAME_CARD.getType().toString());
        if (modifyCard == null || modifyCard.size() != 1 || modifyCard.get(0) == null) {
            throw new BusinessException("改名卡数据异常");
        }
        return userBackpackDao.countNumber(uid, modifyCard.get(0).getId());
    }

    @Override
    public List<BadgeResp> allBadgeList(Long uid) {
        List<ItemConfig> badges = itemCache.getByType(ItemConfigTypeEnum.BADGE.getType().toString());
        SummaryInfoDTO summaryInfoDTO = userSummaryCache.get(uid);
        return UserAdapter.buildBadgeResp(badges, summaryInfoDTO);
    }

    @Override
    public boolean useBackpackItem(Long renameCardId) {
        return userBackpackDao.useOne(renameCardId);
    }

    @Override
    public UserBackpack getOneBackpackByItemType(Long uid, Integer type) {
        List<ItemConfig> itemConfigs = itemCache.getByType(type.toString());
        if (CollectionUtils.isEmpty(itemConfigs)) {
            throw new BusinessException("物品信息异常");
        }
        return userBackpackDao.getFirstNotUsedItemByUidAndItemId(uid, itemConfigs.get(0).getId());
    }
}
