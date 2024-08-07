package com.meteor.chat.event;

import com.meteor.chat.common.domain.dto.MsgMarkDTO;
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
