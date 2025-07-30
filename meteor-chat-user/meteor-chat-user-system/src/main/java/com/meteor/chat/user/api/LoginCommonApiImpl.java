package com.meteor.chat.user.api;

import com.meteor.chat.api.user.LoginCommonApi;
import com.meteor.chat.api.user.dto.LoginSuccessDTO;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.user.service.LoginService;
import com.meteor.chat.api.UserLoginApi;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
@RestController
public class LoginCommonApiImpl implements LoginCommonApi, UserLoginApi {

    @Resource
    private LoginService loginService;

    @Override
    public Long validToken(String token) {
        return loginService.getValidUid(token);
    }

    @Override
    public String login(Long uid) {
        return loginService.login(uid);
    }

    @Override
    public UserInfoDTO loginSuccess(LoginSuccessDTO loginSuccessDTO) {
        // 可能需要更新用户信息，在线人数，并且返回用户的基本信息
        return loginService.successLogin(loginSuccessDTO);
    }

    @Override
    public void offLine(Long uid) {
        loginService.offLine(uid);
    }
}
