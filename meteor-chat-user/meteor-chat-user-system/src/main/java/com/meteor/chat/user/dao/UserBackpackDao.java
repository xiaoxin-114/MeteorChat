package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.enums.YesOrNoEnum;
import com.meteor.chat.user.domain.entity.UserBackpack;
import com.meteor.chat.user.mapper.UserBackpackMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {
    public UserBackpack getByIdempotent(String idempotent) {
        LambdaQueryWrapper<UserBackpack> queryWrapper = new LambdaQueryWrapper<UserBackpack>().eq(UserBackpack::getIdempotent, idempotent);
        return getOne(queryWrapper);
    }

    public long getCountByUidAndItemId(Long uid, Long itemId) {
        LambdaQueryWrapper<UserBackpack> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserBackpack::getUid, uid).eq(UserBackpack::getItemId, itemId);
        return this.count(queryWrapper);
    }

    public Long countNumber(Long uid, Long itemId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getCode())
                .count();
    }

    public List<UserBackpack> listByUidsAndItems(List<Long> uids, List<Long> items) {
        return lambdaQuery()
                .in(UserBackpack::getUid, uids)
                .in(UserBackpack::getItemId, items)
                .eq(UserBackpack::getStatus, CommonConstants.USER_BACK_PACK_NOT_USED)
                .list();
    }

    public List<UserBackpack> listByUidAndItem(Long uid, Long renameCardId) {
        return lambdaQuery()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, renameCardId)
                .eq(UserBackpack::getStatus, CommonConstants.USER_BACK_PACK_NOT_USED)
                .orderByAsc(UserBackpack::getCreateTime)
                .list();
    }

    public boolean useOne(Long id) {
        // 因为是采用乐观锁的方式进行修改，所以需要使用条件背包id和使用状态过滤物品
        // 如果直接使用updateById，无论之前这个物品是否已经使用，都会返回true
        return lambdaUpdate().eq(UserBackpack::getId, id)
                .eq(UserBackpack::getStatus, CommonConstants.USER_BACK_PACK_NOT_USED)
                .set(UserBackpack::getStatus, CommonConstants.USER_BACK_PACK_USED)
                .update();
    }

    public UserBackpack getFirstNotUsedItemByUidAndItemId(Long uid, Long id) {
        return lambdaQuery().eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, id)
                .eq(UserBackpack::getStatus, CommonConstants.USER_BACK_PACK_NOT_USED)
                .last("limit 1")
                .one();
    }
}
