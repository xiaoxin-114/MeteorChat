package com.meteor.chat.api.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 获取群聊消息相关信息的所需参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomMsgReqDTO implements Serializable {
    private Long lastMsgId;

    private Date readTime;

    private Long roomId;
}
