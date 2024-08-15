package com.meteor.chat.msg.service.adapter;


import com.meteor.chat.common.domain.dto.MsgReadInfoDTO;
import com.meteor.chat.common.domain.dto.msg.TextMsgReq;
import com.meteor.chat.common.domain.entity.Contact;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.MessageMark;
import com.meteor.chat.common.domain.enums.DeleteStatusEunm;
import com.meteor.chat.common.domain.enums.MessageMarkTypeEnum;
import com.meteor.chat.common.domain.enums.MessageTypeEnum;
import com.meteor.chat.common.domain.vo.ChatMessageResp;
import com.meteor.chat.common.domain.vo.req.ChatMessageReq;
import com.meteor.chat.msg.service.handler.msg.AbstractMsgHandler;
import com.meteor.chat.msg.service.handler.msg.MsgHandlerFactory;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * 生成消息的基本信息
     * @param req
     * @param uid
     * @return
     */
    public static Message buildMessage(ChatMessageReq req, Long uid) {
        Message message = new Message();
        message.setRoomId(req.getRoomId());
        message.setFromUid(uid);
        message.setType(req.getMsgType());
        message.setStatus(DeleteStatusEunm.NORAML.getCode());
        return message;
    }

    /**
     * 构建好友同意后的自动发送的消息
     * 不需要发送目标，是因为roomid已经可以查询到目标了
     * 不需要发送者，因为当前线程就包含了发送者消息
     * @param roomId 房间号
     * @param content 同意内容，如果好友申请有消息则返回好友申请中的消息，没有则默认
     *                ”已通过好友申请，开始和我聊天把“
     * @return
     */
    public static ChatMessageReq buildApprovalMsg(Long roomId, String content) {
        ChatMessageReq chatMessageReq = new ChatMessageReq();
        chatMessageReq.setRoomId(roomId);
        chatMessageReq.setMsgType(MessageTypeEnum.TEXT.getType());
        TextMsgReq textMsgReq = new TextMsgReq();
        chatMessageReq.setBody(textMsgReq);
        textMsgReq.setContent(content);
        return chatMessageReq;
    }

    /**
     * 创建聊天室成员变动的系统信息，新增或移除
     * @param roomId 聊天室id
     * @param content 消息的内容
     * @return
     */
    public static ChatMessageReq buildMemberChange(Long roomId, String content) {
        return ChatMessageReq.builder()
                .roomId(roomId)
                .body(content)
                .msgType(MessageTypeEnum.SYSTEM.getType())
                .build();
    }

    /**
     * 封装消息数据，返回给前端
     * @param messageList
     * @param markList
     * @param receiveUid
     * @return
     */
    public static List<ChatMessageResp> buildChatMessageResp(List<Message> messageList,
                                                                  List<MessageMark> markList,
                                                                  Long receiveUid) {
        if (CollectionUtils.isEmpty(messageList)) {
            return null;
        }
        Map<Long, List<MessageMark>> markMap = markList.stream().collect(Collectors.groupingBy(MessageMark::getMsgId));
        return messageList.stream().map(message -> {
            ChatMessageResp chatMessageResp = new ChatMessageResp();
            chatMessageResp.setFromUser(buildFormUser(message));
            List<MessageMark> messageMark = markMap.getOrDefault(message.getId(), new ArrayList<>());
            chatMessageResp.setMessage(buildChatMessage(message, messageMark, receiveUid));
            return chatMessageResp;
        }).collect(Collectors.toList());
    }

    private static ChatMessageResp.Message buildChatMessage(Message message, List<MessageMark> messageMark, Long receiveUid) {
        ChatMessageResp.Message result = new ChatMessageResp.Message();
        result.setId(message.getId());
        result.setRoomId(message.getRoomId());
        result.setType(message.getType());
        result.setSendTime(message.getCreateTime());
        AbstractMsgHandler msgHandler = MsgHandlerFactory.getStrategyNotNull(message.getType());
        result.setBody(msgHandler.buildMessageBody(message));
        result.setMessageMark(buildMessageMark(messageMark, receiveUid));
        return result;
    }

    private static ChatMessageResp.MessageMark buildMessageMark(List<MessageMark> messageMarkList, Long receiveUid) {
        ChatMessageResp.MessageMark messageMark = new ChatMessageResp.MessageMark();
        Map<Integer, List<MessageMark>> markMap = messageMarkList.stream().collect(Collectors.groupingBy(MessageMark::getType));
        List<MessageMark> likeList = markMap.getOrDefault(MessageMarkTypeEnum.LIKE.getCode(), new ArrayList<>());
        List<MessageMark> unLikeList = markMap.getOrDefault(MessageMarkTypeEnum.UNLIKE.getCode(), new ArrayList<>());
        messageMark.setLikeCount(likeList.size());
        messageMark.setUserLike(likeList.stream().anyMatch(mark -> mark.getUid().equals(receiveUid)) ? 1 : 0);
        messageMark.setDislikeCount(unLikeList.size());
        messageMark.setUserDislike(unLikeList.stream().anyMatch(mark -> mark.getUid().equals(receiveUid)) ? 1 : 0);
        return messageMark;
    }

    private static ChatMessageResp.UserInfo buildFormUser(Message message) {
        ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
        userInfo.setUid(message.getFromUid());
        return userInfo;
    }
}
