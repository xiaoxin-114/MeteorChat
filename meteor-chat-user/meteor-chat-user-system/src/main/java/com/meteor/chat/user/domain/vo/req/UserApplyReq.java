package com.meteor.chat.user.domain.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("申请好友的请求模型")
@Data
public class UserApplyReq {
    @ApiModelProperty("申请消息")
    private String msg;
    @ApiModelProperty("申请添加好友的目标id")
    @NotNull
    private Long targetUid;
}
