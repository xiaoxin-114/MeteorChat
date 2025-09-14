package com.meteor.chat.msg.domain.vo;

import com.meteor.chat.mybatis.domain.CursorPageBaseReq;
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
public class MessageReadCursorPageReq extends CursorPageBaseReq {
    @Schema(description = "消息id")
    @NotNull
    private Long msgId;

    @Schema(description = "查询类型 1已读 2未读")
    @NotNull
    private Integer searchType;
}
