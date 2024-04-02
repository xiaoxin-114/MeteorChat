package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meteor.chat.common.domain.entity.User;

public interface UserDao extends IService<User> {
    User getByOpenId(String openId);
}
