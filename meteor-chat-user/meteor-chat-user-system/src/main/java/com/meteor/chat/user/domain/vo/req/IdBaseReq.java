package com.meteor.chat.user.domain.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description ="获取详情请求")
@Data
public class IdBaseReq {
    @NotNull
    @Schema(description ="获取详情的id")
    private Long id;
}
