package com.meteor.chat.user.event;

import com.meteor.chat.user.domain.entity.UserBackpack;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class ItemReceiveEvent extends ApplicationEvent {
    private UserBackpack userBackpack;

    public ItemReceiveEvent(Object source, UserBackpack userBackpack) {
        super(source);
        this.userBackpack = userBackpack;
    }
}
