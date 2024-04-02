package com.meteor.chat.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meteor.chat.common.domain.entity.User;

public interface UserService {
    void register(User user);
}
