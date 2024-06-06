package com.meteor.chat.event.listener;

import com.meteor.chat.event.NewUserApplyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NewUserApplyListener
{
    // 向被申请好友的用户发送消息
    @EventListener(classes = NewUserApplyEvent.class)
    public void snedMsgToTarget(NewUserApplyEvent event){

    }
}
