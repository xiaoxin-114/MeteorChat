package com.meteor.chat.event;

import com.meteor.chat.common.domain.dto.MessageRecallDTO;
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
