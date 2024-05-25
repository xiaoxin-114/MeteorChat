package com.meteor.chat.event.listener;

import com.meteor.chat.event.NewUserApplyEvent;
import org.springframework.context.event.EventListener;

public class NewUserApplyListener
{
    // 向被申请好友的用户发送消息
    @EventListener(classes = NewUserApplyEvent.class)
    public void snedMsgToTarget(NewUserApplyEvent event){

    }
}
