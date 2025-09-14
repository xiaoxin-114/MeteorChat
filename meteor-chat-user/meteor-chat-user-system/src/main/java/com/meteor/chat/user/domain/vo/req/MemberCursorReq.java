package com.meteor.chat.user.domain.vo.req;

import com.meteor.chat.mybatis.domain.CursorPageBaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description ="分页查询群聊成员列表请求")
@Data
public class MemberCursorReq extends CursorPageBaseReq {
    @NotNull
    @Schema(description ="群聊聊天室id")
    private Long roomId;
}
