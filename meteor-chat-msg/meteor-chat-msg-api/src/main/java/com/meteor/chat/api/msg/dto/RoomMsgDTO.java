package com.meteor.chat.api.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 描述群聊的消息相关信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomMsgDTO implements Serializable {
    /**
     * 群聊id
     */
    private Long roomId;
    /**
     * 群聊最新消息id
     */
    private Long lastMsgId;
    /**
     * 群聊最新消息展示的内容
     */
    private String msgText;
    /**
     * 未读消息数量
     */
    private Long unreadCount;
}
