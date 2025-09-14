package com.meteor.chat.msg.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MsgRecallReq {
    @NotNull
    @Schema(description ="消息id")
    private Long msgId;
    @NotNull
    @Schema(description ="会话id")
    private Long roomId;
}
