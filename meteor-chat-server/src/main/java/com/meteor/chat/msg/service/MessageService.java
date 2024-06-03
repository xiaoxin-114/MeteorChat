package com.meteor.chat.msg.service;

import com.meteor.chat.common.domain.vo.ChatMessageReadResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.MessageReadCursorPageReq;

public interface MessageService {
    CursorPageBaseResp<ChatMessageReadResp> cursorPageMsgReader(MessageReadCursorPageReq req, Long uid);
}
