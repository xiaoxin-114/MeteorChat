package com.meteor.chat.user.domain.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description ="拉黑用户请求模型")
@Data
public class BlackReq {
    @Schema(description ="拉黑用户id")
    @NotNull(message = "拉黑用户id不能为空")
    private Long uid;
}
