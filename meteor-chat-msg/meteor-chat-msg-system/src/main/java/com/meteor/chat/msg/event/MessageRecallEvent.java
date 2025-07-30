package com.meteor.chat.msg.event;

import com.meteor.chat.msg.domain.dto.MessageRecallDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageRecallEvent extends ApplicationEvent {
    private MessageRecallDTO messageRecallDTO;

    public MessageRecallEvent(MessageRecallDTO dto, Object source) {
        super(source);
        this.messageRecallDTO = dto;
    }
}
