package com.meteor.chat.user.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description ="徽章图鉴的返回值")
public class BadgeResp {

    @Schema(description = "徽章id")
    private Long id;

    @Schema(description = "徽章图标")
    private String img;

    @Schema(description = "徽章描述")
    private String describe;

    @Schema(description = "是否拥有 0否 1是")
    private Integer obtain;

    @Schema(description = "是否佩戴  0否 1是")
    private Integer wearing;
}
