package com.meteor.chat.event;

import com.meteor.chat.common.domain.entity.UserBackpack;
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
