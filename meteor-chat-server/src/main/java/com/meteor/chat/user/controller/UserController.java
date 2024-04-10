package com.meteor.chat.user.controller;

import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.domain.vo.UserInfoVO;
import com.meteor.chat.common.util.UserContext;
import com.meteor.chat.user.service.UserService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/userInfo")
    public ApiResult<UserInfoVO> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(UserContext.get().getUid()));
    }
}
