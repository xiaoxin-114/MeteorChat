package com.meteor.chat.user.service.impl;

import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.service.UserService;

import javax.annotation.Resource;

public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public void register(User user) {
        userDao.save(user);
        //todo 事件推送
    }
}
