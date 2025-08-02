package com.meteor.chat.api.user;

import com.meteor.chat.api.user.constants.ApiConstants;
import com.meteor.chat.api.user.dto.LoginSuccessDTO;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.common.constants.RpcConstants;
import com.meteor.chat.common.result.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = RpcConstants.USER_SERVER_NAME)
public interface LoginCommonApi {


    /**
     * 根据用户id获取token
     * @param uid
     * @return
     */
    @GetMapping(ApiConstants.LOGIN_PREFIX + "/token")
    ApiResult<String> login(@RequestParam("uid") Long uid);

    /**
     * 用户登陆成功后，根据用户id和ip信息来更新用户状态、在线人数和用户信息
     * @param loginSuccessDTO
     * @return
     */
    @PostMapping(ApiConstants.LOGIN_PREFIX)
    ApiResult<UserInfoDTO> loginSuccess(@RequestBody LoginSuccessDTO loginSuccessDTO);

    /**
     * 用户下线，处理用户状态和信息，更新在线列表并发送下线消息
     * @param uid 下线用户id
     */
    @PostMapping(ApiConstants.LOGIN_PREFIX + "/offline/{uid}")
    void offLine(@PathVariable("uid") Long uid);
}