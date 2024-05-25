package com.meteor.chat.event;

import com.meteor.chat.common.domain.entity.UserApply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewFriendEvent extends ApplicationEvent {

    private UserApply userApply;

    private Long roomId;

    public NewFriendEvent(Object source, UserApply userApply, Long roomId) {
        super(source);
        this.userApply = userApply;
        this.roomId = roomId;
    }
}
