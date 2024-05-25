package com.meteor.chat.user.controller;

import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.domain.vo.UserInfoVO;
import com.meteor.chat.common.domain.vo.req.BlackReq;
import com.meteor.chat.common.exception.CommonErrorEnum;
import com.meteor.chat.common.exception.ErrorEnum;
import com.meteor.chat.common.util.UserContext;
import com.meteor.chat.user.service.UserService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/capi/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/userInfo")
    public ApiResult<UserInfoVO> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(UserContext.get().getUid()));
    }

    @PutMapping("/black")
    public ApiResult black(@RequestBody @Valid BlackReq req) {
        Long uid = UserContext.get().getUid();
        if (!userService.isAdmin(uid)) {
            return ApiResult.fail(CommonErrorEnum.NOT_PERMITTED);
        }
        userService.black(req.getBlackId());
        return ApiResult.success();
    }
}
