package com.meteor.chat.msg.service.handler;

import com.meteor.chat.common.domain.dto.msg.EmojisMsgDTO;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.MessageExtra;
import com.meteor.chat.common.domain.enums.MessageTypeEnum;
import com.meteor.chat.msg.dao.MessageDao;

import javax.annotation.Resource;
import java.util.Optional;

public class EmojiMsgHandler extends AbstractMsgHandler<EmojisMsgDTO>{
    @Resource
    private MessageDao messageDao;

    private final MessageTypeEnum MESSAGE_TYPE = MessageTypeEnum.EMOJI;

    @Override
    void saveMessageExtra(Message message, EmojisMsgDTO body) {
        MessageExtra messageExtra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        messageExtra.setEmojisMsgDTO(body);
        update.setExtra(messageExtra);
        messageDao.updateById(update);
    }

    @Override
    MessageTypeEnum getMsgType() {
        return MESSAGE_TYPE;
    }

    @Override
    public String messageText(Message message) {
        return "[表情]";
    }
}
