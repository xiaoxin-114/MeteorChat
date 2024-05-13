package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.mapper.UserMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends ServiceImpl<UserMapper, User> {

    public User getByOpenId(String openId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>().eq(User::getOpenId, openId);
        return getOne(queryWrapper);
    }
}
