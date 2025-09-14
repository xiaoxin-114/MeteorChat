package com.meteor.chat.user.domain.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description ="申请好友的请求模型")
@Data
public class UserApplyReq {
    @Schema(description ="申请消息")
    private String msg;
    @Schema(description ="申请添加好友的目标id")
    @NotNull
    private Long targetUid;
}
