package com.meteor.chat.common.domain.vo.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRegisterReq {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
