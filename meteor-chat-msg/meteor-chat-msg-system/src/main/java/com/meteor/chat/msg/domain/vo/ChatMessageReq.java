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
public class ChatMessageReq {

    @NotNull
    @Schema(description = "房间id")
    private Long roomId;

    @NotNull
    @Schema(description = "消息类型")
    private Integer msgType;

    /**
     * @see com.meteor.chat.msg.domain.dto.body
     */
    @NotNull
    @Schema(description = "消息内容，类型不同传值不同，见https://www.yuque.com/snab/mallcaht/rkb2uz5k1qqdmcmd")
    private Object body;
}
