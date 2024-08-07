package com.meteor.chat.common.domain.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("标记消息请求对象")
public class MsgMarkReq {
    @ApiModelProperty("操作类型，1表示生效，2表示取消")
    @NotNull
    private Integer actType;
    @ApiModelProperty("标记类型，1表示点赞，2表示点踩")
    @NotNull
    /**
     * @see com.meteor.chat.common.domain.enums.MessageMarkTypeEnum
     */
    private Integer markType;
    @ApiModelProperty("消息id")
    @NotNull
    private Long msgId;
}
