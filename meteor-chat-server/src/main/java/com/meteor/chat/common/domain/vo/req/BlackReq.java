package com.meteor.chat.common.domain.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("拉黑用户请求模型")
@Data
public class BlackReq {
    @ApiModelProperty("拉黑用户id")
    @NotNull(message = "拉黑用户id不能为空")
    private Long uid;
}
