package com.meteor.chat.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.vo.UserInfoVO;

public interface UserService {
    void register(User user);

    UserInfoVO getUserInfo(Long uid);

    void wearBadge(Long uid, Long itemId);

    /**
     * 判断用户是否有拉黑的权限（系统管理员，群聊管理员都能拉黑）
     * @param uid
     * @return
     */
    boolean isAdmin(Long uid);
}
