package com.meteor.chat.event.listener;

import com.meteor.chat.event.NewFriendEvent;
import com.meteor.chat.chat.service.RoomService;
import com.meteor.chat.websocket.domain.enums.WSRespTypeEnum;
import com.meteor.chat.websocket.domain.vo.WSBaseResp;
import com.meteor.chat.websocket.service.WebSocketService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class NewFriendEventListener {

    @Resource
    private WebSocketService webSocketService;

    @EventListener(value = NewFriendEvent.class)
    public void sendMsg(NewFriendEvent event) {
        // todo 好友申请同意后发送消息
    }
}
