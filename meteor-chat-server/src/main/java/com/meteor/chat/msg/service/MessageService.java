package com.meteor.chat.msg.service;

import com.meteor.chat.common.domain.dto.MsgReadInfoDTO;
import com.meteor.chat.common.domain.vo.ChatMessageReadResp;
import com.meteor.chat.common.domain.vo.ChatMessageResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.ChatMessageReq;
import com.meteor.chat.common.domain.vo.req.MessageReadCursorPageReq;
import com.meteor.chat.common.domain.vo.req.MessageReadInfoReq;

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
     * @return
     */
    ChatMessageResp getMessageResp(Long msgId);
}
