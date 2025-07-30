package com.meteor.chat.msg.service.handler.msg;

import com.meteor.chat.msg.domain.dto.body.ImgMsgDTO;
import com.meteor.chat.msg.domain.entity.Message;
import com.meteor.chat.msg.domain.entity.MessageExtra;
import com.meteor.chat.msg.enums.MessageTypeEnum;
import com.meteor.chat.msg.dao.MessageDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class ImgMsgHandler extends AbstractMsgHandler<ImgMsgDTO> {
    @Resource
    private MessageDao messageDao;

    private final MessageTypeEnum MESSAGE_TYPE = MessageTypeEnum.IMG;

    @Override
    MessageTypeEnum getMsgType() {
        return MESSAGE_TYPE;
    }

    @Override
    void saveMessageExtra(Message message, ImgMsgDTO body) {
        MessageExtra messageExtra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        messageExtra.setImgMsgDTO(body);
        update.setExtra(messageExtra);
        messageDao.updateById(update);
    }

    @Override
    public String messageText(Message message) {
        return "[图片]";
    }

    @Override
    public String replyMsgText(Message message) {
        return "图片";
    }

    @Override
    public Object buildMessageBody(Message message) {
        return message.getExtra().getImgMsgDTO();
    }
}
