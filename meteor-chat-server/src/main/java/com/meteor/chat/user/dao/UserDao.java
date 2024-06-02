package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao extends ServiceImpl<UserMapper, User> {

    public User getByOpenId(String openId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>().eq(User::getOpenId, openId);
        return getOne(queryWrapper);
    }

    public List<User> getByName(String name) {
        return lambdaQuery().eq(User::getName, name).list();
    }

    public void rename(Long uid, String name) {
        lambdaUpdate().eq(User::getId, uid).set(User::getName, name).update();
    }

    public void wearBadge(Long uid, Long itemId) {
        lambdaUpdate().eq(User::getId, uid)
                .set(User::getItemId, itemId)
                .update();
    }
}
