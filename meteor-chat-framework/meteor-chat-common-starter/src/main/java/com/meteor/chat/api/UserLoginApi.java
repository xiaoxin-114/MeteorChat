package com.meteor.chat.api;

import com.meteor.chat.common.constants.RpcConstants;
import com.meteor.chat.common.result.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = RpcConstants.USER_SERVER_NAME)
public interface UserLoginApi {

    String VALID_TOKEN_URL = "/api/user/login/token";
    String VALID_TOKEN_URI = "http://" + RpcConstants.USER_SERVER_NAME + VALID_TOKEN_URL;

    @GetMapping(VALID_TOKEN_URL)
    ApiResult<Long> validToken(@RequestParam("token") String token);
}
