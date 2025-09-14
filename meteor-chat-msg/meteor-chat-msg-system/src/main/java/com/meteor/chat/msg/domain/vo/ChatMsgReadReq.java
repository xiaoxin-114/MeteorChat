package com.meteor.chat.msg.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class ChatMsgReadReq {
    @NotNull
    @Schema(description = "用户读取的房间号id")
    private Long roomId;
}
