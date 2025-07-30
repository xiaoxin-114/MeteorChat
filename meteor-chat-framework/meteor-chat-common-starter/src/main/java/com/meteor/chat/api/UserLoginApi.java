package com.meteor.chat.api;

import com.meteor.chat.common.constants.RpcConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = RpcConstants.USER_SERVER_NAME)
public interface UserLoginApi {
    @PostMapping("/api/user/login/token")
    Long validToken(@RequestBody String token);
}
