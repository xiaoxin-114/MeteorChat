package com.meteor.chat.msg.service.adapter;

import com.meteor.chat.common.domain.dto.MsgReadInfoDTO;
import com.meteor.chat.common.domain.entity.Contact;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.enums.DeleteStatusEunm;
import com.meteor.chat.common.domain.vo.req.ChatMessageReq;

import java.util.List;

public class MsgAdapter {
    /**
     * 封装消息的已读未读数量对象
     * @param msg 消息
     * @param contacts 会话列表
     * @return
     */
    public static MsgReadInfoDTO buildMsgReadInfoDTO(Message msg, List<Contact> contacts) {
        int readCount = (int) contacts.stream()
                .filter(contact -> contact.getReadTime().compareTo(msg.getCreateTime()) > -1)
                .count();
        return MsgReadInfoDTO.builder()
                .msgId(msg.getId())
                .readCount(readCount)
                .unReadCount(contacts.size() - readCount).build();
    }

    public static Message buildMessage(ChatMessageReq req, Long uid) {
        Message message = new Message();
        message.setRoomId(req.getRoomId());
        message.setFromUid(uid);
        message.setType(req.getMsgType());
        message.setStatus(DeleteStatusEunm.NORAML.getCode());
        return message;
    }
}
