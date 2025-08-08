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
public interface UserRoleCommonApi {


    ApiResult<Boolean> isSuperAdmin(Long uid);
}