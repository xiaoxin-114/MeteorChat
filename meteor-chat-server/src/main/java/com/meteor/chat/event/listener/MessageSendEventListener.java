package com.meteor.chat.event.listener;

import com.meteor.chat.common.constants.MQConstant;
import com.meteor.chat.common.domain.dto.MsgSendMessageDTO;
import com.meteor.chat.consumer.ConsumerExecutor;
import com.meteor.chat.event.MessageSendEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessageSendEventListener {

    @Resource
    private ConsumerExecutor consumerExecutor;

    @EventListener(value = MessageSendEvent.class)
    public void pushMsg(MessageSendEvent event) {
        Long msgId = event.getMsgId();
        consumerExecutor.execute(MQConstant.SEND_MSG_TOPIC, new MsgSendMessageDTO(msgId));
    }
}
