package com.meteor.chat.user.domain.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description ="登录密码请求模型")
public class LoginPasswordReq {
    @Schema(description ="用户名")
    @NotNull(message = "用户名不能为空")
    private String username;
    @Schema(description ="密码")
    @NotNull(message = "密码不能为空")
    private String password;

}
