package com.meteor.chat.user.api;

import com.meteor.chat.api.UserLoginApi;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.user.service.LoginService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class UserLoginApiImpl implements UserLoginApi {
    @Resource
    private LoginService loginService;

    @Override
    public ApiResult<Long> validToken(String token) {
        return ApiResult.success(loginService.getValidUid(token));
    }
}
