package com.meteor.chat.event;

import com.meteor.chat.common.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class BlackUserEvent extends ApplicationEvent {
    private User user;

    public BlackUserEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
