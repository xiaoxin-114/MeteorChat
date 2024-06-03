package com.meteor.chat.msg.service.impl;

import com.meteor.chat.chat.dao.ContactDao;
import com.meteor.chat.common.domain.vo.ChatMessageReadResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.MessageReadCursorPageReq;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.msg.service.MessageService;

import javax.annotation.Resource;

public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageDao messageDao;

    @Resource
    private ContactDao contactDao;

    @Override
    public CursorPageBaseResp<ChatMessageReadResp> cursorPageMsgReader(MessageReadCursorPageReq req, Long uid) {
        return null;
    }
}
