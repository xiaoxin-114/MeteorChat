package com.meteor.chat.room.domain.vo;

import com.meteor.chat.common.domain.CursorPageBaseReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

@ApiModel("分页查询群聊成员列表请求")
@Data
public class RoomMemberCursorReq extends CursorPageBaseReq {
    @NotNull
    @ApiModelProperty("群聊聊天室id")
    private Long roomId;
}
