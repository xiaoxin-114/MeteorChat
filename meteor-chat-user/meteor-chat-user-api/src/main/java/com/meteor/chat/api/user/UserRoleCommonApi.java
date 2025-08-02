package com.meteor.chat.api.user;

import com.meteor.chat.api.user.constants.ApiConstants;
import com.meteor.chat.common.constants.RpcConstants;
import com.meteor.chat.common.result.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户权限接口
 */
@FeignClient(name = RpcConstants.USER_SERVER_NAME)
public interface UserRoleCommonApi {

    @GetMapping(ApiConstants.ROLE_PREFIX + "/super")
    ApiResult<Boolean> isSuperAdmin(@RequestParam("uid") Long uid);
}