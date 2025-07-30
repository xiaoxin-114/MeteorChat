package com.meteor.chat.msg.service;


import com.meteor.chat.api.msg.dto.ChatMessageResp;
import com.meteor.chat.api.msg.dto.MemberAddMsgDTO;
import com.meteor.chat.api.msg.dto.RoomMsgDTO;
import com.meteor.chat.api.msg.dto.RoomMsgReqDTO;
import com.meteor.chat.msg.domain.dto.MsgReadInfoDTO;
import com.meteor.chat.msg.domain.vo.*;
import com.meteor.chat.common.domain.CursorPageBaseResp;

import java.util.List;

public interface MessageService {
    /**
     * 分页查询消息的已读/未读列表
     * @param req
     * @return
     */
    CursorPageBaseResp<ChatMessageReadResp> cursorPageMsgReader(MessageReadCursorPageReq req);

    /**
     * 查询消息的已读和未读数量
     * @param req 包含消息列表
     * @return
     */
    List<MsgReadInfoDTO> countReadAndUnRead(MessageReadInfoReq req, Long uid);

    /**
     * 发送消息
     * @param request
     * @param uid
     * @return 消息id
     */
    Long sendMsg(ChatMessageReq request, Long uid);

    /**
     * 根据消息id获取消息的所有详情
     * @param msgId
     * @param receiveUid 接收到信息的用户id
     * @return
     */
    ChatMessageResp getMessageResp(Long msgId, Long receiveUid);

    /**
     * 游标分页获取消息列表
     * @param req
     * @param uid
     * @return
     */
    CursorPageBaseResp<ChatMessageResp> cursorChatMessageResp(MessageCursorReq req, Long uid);

    /**
     * 撤回消息
     * @param req
     */
    void recall(MsgRecallReq req, Long uid);

    /**
     * 读取消息
     * @param req
     * @param uid
     */
    void readMsg(ChatMsgReadReq req, Long uid);

    void markMsg(MsgMarkReq req, Long uid);

    /**
     * 发送群成员邀请成功的消息
     * @param memberAddMsgDTO 群id，邀请人id，被邀请人id列表
     */
    void sendMemberAddMsg(MemberAddMsgDTO memberAddMsgDTO);

    /**
     * 发送群群聊成员减少的消息，可能是退群/被移除
     * @param roomId 群id
     * @param uid 退出群的成员id
     */
    void sendMemberSubMsg(Long roomId, Long uid, String content);

    /**
     * 根据消息id，获取消息缩略信息，以及群聊的未读消息数量
     * @param reqList
     * @return
     */
    List<RoomMsgDTO> getRoomMsgList(List<RoomMsgReqDTO> reqList);

    /**
     * 删除群聊所有消息
     * @param roomId 群id
     */
    void removeRoomMsg(Long roomId);
}
