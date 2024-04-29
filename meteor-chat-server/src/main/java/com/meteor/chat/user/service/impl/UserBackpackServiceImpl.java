package com.meteor.chat.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meteor.chat.common.annotation.RedissonLock;
import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.domain.entity.ItemConfig;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.entity.UserBackpack;
import com.meteor.chat.common.domain.enums.IdempotenceCodeEnum;
import com.meteor.chat.common.domain.enums.ItemConfigTypeEnum;
import com.meteor.chat.event.ItemReceiveEvent;
import com.meteor.chat.user.dao.UserBackpackDao;
import com.meteor.chat.user.service.UserBackpackService;
import com.meteor.chat.user.service.cache.ItemCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserBackpackServiceImpl implements UserBackpackService {

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private UserBackpackDao userBackpackDao;
    @Resource
    private ItemCache itemCache;


    @Override
    public void issueItem(Long uid, Long itemId, IdempotenceCodeEnum codeEnum, Long bussinessId) {
        // 幂等判断
        String idempotent = getIdempotent(itemId, codeEnum, bussinessId);
        doIssueItem(idempotent, uid, itemId);
    }
    @RedissonLock(key = "#idempotent", time = 5000)
    public void doIssueItem(String idempotent, Long uid, Long itemId) {
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
        if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(itemIdList)) {
            return null;
        }
        LambdaQueryWrapper<UserBackpack> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserBackpack::getStatus, CommonConstants.USER_BACK_PACK_NOT_USED)
                .in(UserBackpack::getUid, list)
                .in(UserBackpack::getItemId, itemIdList);
        return userBackpackDao.list(queryWrapper);
    }

    private String getIdempotent(Long itemId, IdempotenceCodeEnum codeEnum, Long bussinessId) {
        return String.format("%s_%s_%s", itemId, codeEnum.getDescr(), bussinessId);
    }

}
