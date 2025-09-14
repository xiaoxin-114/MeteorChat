package com.meteor.chat.msg.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
@Schema(description ="标记消息请求对象")
public class MsgMarkReq {
    @Schema(description ="操作类型，1表示生效，2表示取消")
    @NotNull
    private Integer actType;
    @Schema(description ="标记类型，1表示点赞，2表示点踩")
    @NotNull
    /**
     * @see com.meteor.chat.common.domain.enums.MessageMarkTypeEnum
     */
    private Integer markType;
    @Schema(description ="消息id")
    @NotNull
    private Long msgId;
}
