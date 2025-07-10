package com.meteor.chat.common.domain.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("登录密码请求模型")
public class LoginPasswordReq {
    @ApiModelProperty("用户名")
    @NotNull(message = "用户名不能为空")
    private String username;
    @ApiModelProperty("密码")
    @NotNull(message = "密码不能为空")
    private String password;

}
