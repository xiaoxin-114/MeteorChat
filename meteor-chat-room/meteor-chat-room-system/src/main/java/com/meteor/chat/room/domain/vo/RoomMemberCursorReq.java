package com.meteor.chat.room.domain.vo;

import com.meteor.chat.mybatis.domain.CursorPageBaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Schema(description ="分页查询群聊成员列表请求")
@Data
public class RoomMemberCursorReq extends CursorPageBaseReq {
    @NotNull
    @Schema(description ="群聊聊天室id")
    private Long roomId;
}
