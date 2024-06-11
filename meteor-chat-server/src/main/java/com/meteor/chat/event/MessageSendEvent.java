package com.meteor.chat.event;

import org.springframework.context.ApplicationEvent;

public class MessageSendEvent extends ApplicationEvent {

    private Long msgId;

    public MessageSendEvent(Object source, Long msgId) {
        super(source);
        this.msgId = msgId;
    }
}
