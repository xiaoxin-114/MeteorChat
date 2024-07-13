package com.meteor.chat.msg.service.handler;

import com.meteor.chat.common.domain.dto.msg.FileMsgDTO;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.MessageExtra;
import com.meteor.chat.common.domain.enums.MessageTypeEnum;
import com.meteor.chat.msg.dao.MessageDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class FileMsgHandler extends AbstractMsgHandler<FileMsgDTO> {
    @Resource
    private MessageDao messageDao;

    private final MessageTypeEnum MESSAGE_TYPE = MessageTypeEnum.FILE;

    @Override
    MessageTypeEnum getMsgType() {
        return MESSAGE_TYPE;
    }

    @Override
    void saveMessageExtra(Message message, FileMsgDTO body) {
        MessageExtra messageExtra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        messageExtra.setFileMsgDTO(body);
        update.setExtra(messageExtra);
        messageDao.updateById(update);
    }

    @Override
    String messageText(Message message) {
        return "[文件]" + message.getExtra();
    }
}
