package com.meteor.chat.event;

import com.meteor.chat.common.domain.entity.User;
import lombok.Data;
import org.springframework.context.ApplicationEvent;
@Data
public class UserOfflineEvent extends ApplicationEvent {

    private final User user;

    public UserOfflineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
