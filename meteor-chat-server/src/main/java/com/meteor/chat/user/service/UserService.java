package com.meteor.chat.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.vo.UserInfoVO;

public interface UserService {
    void register(User user);

    UserInfoVO getUserInfo(Long uid);
}
