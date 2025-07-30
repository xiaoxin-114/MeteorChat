package com.meteor.chat.user.service;

import com.meteor.chat.api.user.dto.LoginSuccessDTO;
import com.meteor.chat.api.user.dto.UserInfoDTO;

import javax.validation.constraints.NotNull;

public interface LoginService {


    /**
     * 校验token是不是有效
     *
     * @param token
     * @return
     */
    boolean verify(String token);

    /**
     * 刷新token有效期
     *
     * @param token
     */
    void renewalTokenIfNecessary(String token);

    /**
     * 登录成功，获取token
     *
     * @param uid
     * @return 返回token
     */
    String login(Long uid);

    /**
     * 如果token有效，返回uid
     *
     * @param token
     * @return
     */
    Long getValidUid(String token);

    /**
     * 账号密码登录
     * @param username
     * @param password
     * @return token信息
     */
    String loginByPassword(@NotNull(message = "用户名不能为空") String username, @NotNull(message = "密码不能为空") String password);

    /**
     * 用户成功登陆后，更新用户信息和在线人数
     * @param loginSuccessDTO 登陆用户id和ip
     * @return 用户基本信息
     */
    UserInfoDTO successLogin(LoginSuccessDTO loginSuccessDTO);

    /**
     * 用户下线，更新用户状态，发送用户离线通知
     */
    void offLine(Long uid);
}