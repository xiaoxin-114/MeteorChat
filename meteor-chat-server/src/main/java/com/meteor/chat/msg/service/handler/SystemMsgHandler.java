package com.meteor.chat.msg.service.handler;

import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.MessageExtra;
import com.meteor.chat.common.domain.enums.MessageTypeEnum;
import com.meteor.chat.msg.dao.MessageDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class SystemMsgHandler extends AbstractMsgHandler<String> {
    @Resource
    private MessageDao messageDao;

    private final MessageTypeEnum MESSAGE_TYPE = MessageTypeEnum.VIDEO;

    @Override
    MessageTypeEnum getMsgType() {
        return MESSAGE_TYPE;
    }

    @Override
    void saveMessageExtra(Message message, String body) {
        Message update = new Message();
        update.setId(message.getId());
        update.setContent(body);
        messageDao.updateById(update);
    }

    @Override
    String messageText(Message message) {
        return message.getContent();
    }
}
