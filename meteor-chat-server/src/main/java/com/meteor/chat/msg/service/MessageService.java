package com.meteor.chat.msg.service;

import com.meteor.chat.common.domain.dto.MsgReadInfoDTO;
import com.meteor.chat.common.domain.vo.ChatMessageReadResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
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
}
