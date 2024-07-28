package com.meteor.chat.common.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MsgRecallReq {
    @NotNull
    @ApiModelProperty("消息id")
    private Long msgId;
    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;
}
