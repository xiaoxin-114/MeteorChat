package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.UserRole;
import com.meteor.chat.common.mapper.UserRoleMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole>{

    public UserRole getUserRoleByUid(Long uid) {
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<UserRole>().eq(UserRole::getUid, uid);
        return getOne(queryWrapper);
    }
}
