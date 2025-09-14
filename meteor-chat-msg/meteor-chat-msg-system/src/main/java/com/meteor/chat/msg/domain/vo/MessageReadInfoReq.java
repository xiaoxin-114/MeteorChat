package com.meteor.chat.msg.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageReadInfoReq {
    @Schema(description ="消息id集合（只查本人）")
    @Size(max = 20)
    private List<Long> msgIds;
}