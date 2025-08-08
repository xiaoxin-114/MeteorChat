package com.meteor.chat.user.api;

import com.meteor.chat.api.user.LoginCommonApi;
import com.meteor.chat.api.user.dto.LoginSuccessDTO;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.user.service.LoginService;
import com.meteor.chat.api.UserLoginApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@DubboService
public class LoginCommonApiImpl implements LoginCommonApi {

    @Resource
    private LoginService loginService;

    @Override
    public ApiResult<String> login(Long uid) {
        return ApiResult.success(loginService.login(uid));
    }

    @Override
    public ApiResult<UserInfoDTO> loginSuccess(LoginSuccessDTO loginSuccessDTO) {
        // 可能需要更新用户信息，在线人数，并且返回用户的基本信息
        return ApiResult.success(loginService.successLogin(loginSuccessDTO));
    }

    @Override
    public void offLine(Long uid) {
        loginService.offLine(uid);
    }
}
