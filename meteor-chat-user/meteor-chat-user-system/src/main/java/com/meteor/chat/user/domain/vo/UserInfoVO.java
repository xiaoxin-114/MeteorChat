package com.meteor.chat.user.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description ="前端展示用户详情")
public class UserInfoVO {
    @Schema(description ="用户id")
    private Long id;

    @Schema(description ="用户昵称")
    private String name;

    @Schema(description ="用户头像")
    private String avatar;

    @Schema(description ="性别 1为男性，2为女性")
    private Integer sex;

    @Schema(description ="剩余改名次数")
    private Long modifyNameChance;
}
