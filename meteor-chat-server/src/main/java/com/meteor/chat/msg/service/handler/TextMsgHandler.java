package com.meteor.chat.msg.service.handler;

import com.meteor.chat.common.domain.dto.msg.TextMsgDTO;
import com.meteor.chat.common.domain.dto.msg.VideoMsgDTO;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.enums.MessageTypeEnum;
import com.meteor.chat.msg.dao.MessageDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgDTO> {
    @Resource
    private MessageDao messageDao;

    private final MessageTypeEnum MESSAGE_TYPE = MessageTypeEnum.TEXT;

    @Override
    MessageTypeEnum getMsgType() {
        return MESSAGE_TYPE;
    }

    @Override
    void saveMessageExtra(Message message, TextMsgDTO body) {

    }
}
