package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.UserBackpack;
import com.meteor.chat.common.mapper.UserBackpackMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {
    public UserBackpack getByIdempotent(String idempotent) {
        LambdaQueryWrapper<UserBackpack> queryWrapper = new LambdaQueryWrapper<UserBackpack>().eq(UserBackpack::getIdempotent, idempotent);
        return getOne(queryWrapper);
    }

    public int getCountByUidAndItemId(Long uid, Long itemId) {
        LambdaQueryWrapper<UserBackpack> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserBackpack::getUid, uid).eq(UserBackpack::getItemId, itemId);
        return this.count(queryWrapper);
    }
}
