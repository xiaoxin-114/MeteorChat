package com.meteor.chat.room.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("获取详情请求")
@Data
public class IdBaseReq {
    @NotNull
    @ApiModelProperty("获取详情的id")
    private Long id;
}
