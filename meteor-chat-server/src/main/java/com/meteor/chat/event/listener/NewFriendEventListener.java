package com.meteor.chat.event.listener;

import com.meteor.chat.event.NewFriendEvent;
import com.meteor.chat.msg.service.RoomService;
import com.meteor.chat.user.dao.UserFriendDao;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class NewFriendEventListener {

    @EventListener(value = NewFriendEvent.class)
    public void sendMsg(NewFriendEvent event) {
        // todo 好友申请同意后发送消息
    }
}
