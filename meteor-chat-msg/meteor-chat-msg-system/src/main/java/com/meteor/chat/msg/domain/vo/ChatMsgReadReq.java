package com.meteor.chat.msg.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChatMsgReadReq {
    @ApiModelProperty("用户读取的房间号id")
    private Long roomId;
}
