package com.meteor.chat.msg.event;

import com.meteor.chat.msg.domain.dto.MsgMarkDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MsgMarkEvent extends ApplicationEvent {

    private MsgMarkDTO msgMarkDTO;

    public MsgMarkEvent(Object source, MsgMarkDTO dto) {
        super(source);
        this.msgMarkDTO = dto;
    }
}
