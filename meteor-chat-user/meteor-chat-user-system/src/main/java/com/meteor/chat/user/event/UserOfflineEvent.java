package com.meteor.chat.user.event;

import com.meteor.chat.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class UserOfflineEvent extends ApplicationEvent {

    private final User user;

    public UserOfflineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
