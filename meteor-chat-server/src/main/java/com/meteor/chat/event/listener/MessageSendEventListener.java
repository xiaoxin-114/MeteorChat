package com.meteor.chat.event.listener;

import com.meteor.chat.common.domain.dto.MsgSendMessageDTO;
import com.meteor.chat.event.MessageSendEvent;
import com.meteor.chat.rabbitmq.constants.MQConstant;
import com.meteor.chat.rabbitmq.producer.MQProducer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessageSendEventListener {

    @Resource
    private MQProducer mqProducer;

    @EventListener(value = MessageSendEvent.class)
    public void pushMsg(MessageSendEvent event) {
        Long msgId = event.getMsgId();
        mqProducer.sendMsg(MQConstant.SEND_MSG_EXCHANGE, MQConstant.SEND_MSG_ROUTING_KEY, new MsgSendMessageDTO(msgId));
    }
}
