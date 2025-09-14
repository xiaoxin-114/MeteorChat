package com.meteor.chat.room.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
    @Schema(description ="房间id")
    private Long roomId;
    @Schema(description ="房间类型 1群聊 2单聊")
    private Integer type;
    @Schema(description ="是否全员展示的会话 0否 1是")
    private Integer hot_Flag;
    @Schema(description ="最新消息id")
    private Long lastMsgId;
    @Schema(description ="会话名称")
    private String name;
    @Schema(description ="会话头像")
    private String avatar;
    @Schema(description ="房间最后活跃时间(用来排序)")
    private Date activeTime;
}
