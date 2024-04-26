package com.meteor.chat.user.service.impl;

import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.vo.UserInfoVO;
import com.meteor.chat.event.UserRegisterEvent;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.service.UserService;
import com.meteor.chat.user.service.adapter.UserAdapter;
import com.meteor.chat.user.service.cache.UserCache;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private UserCache userCache;

    @Override
    public void register(User user) {
        userDao.save(user);
        //用户注册事件推送
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, user));
    }

    @Override
    public UserInfoVO getUserInfo(Long uid) {
        User userInfo = userCache.getUserInfo(uid);
        // todo 背包改名卡查询，获取用户可改名次数
        return UserAdapter.buildUserInfoResp(userInfo, 1);
    }

    @Override
    public void wearBadge(Long uid, Long itemId) {
        User userInfo = User.builder().id(uid).itemId(itemId).build();
        userDao.updateById(userInfo);
    }
}
