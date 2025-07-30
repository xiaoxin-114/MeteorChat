package com.meteor.chat.user.event;

import com.meteor.chat.user.domain.entity.UserApply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewUserApplyEvent extends ApplicationEvent {
    private UserApply userApply;
    public NewUserApplyEvent(Object source, UserApply userApply) {
        super(source);
        this.userApply = userApply;
    }
}
