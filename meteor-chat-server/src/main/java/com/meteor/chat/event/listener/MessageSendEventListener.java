package com.meteor.chat.event.listener;

import com.meteor.chat.chat.dao.RoomDao;
import com.meteor.chat.chat.service.cache.HotRoomCache;
import com.meteor.chat.chat.service.cache.RoomCache;
import com.meteor.chat.common.constants.MQConstant;
import com.meteor.chat.common.domain.dto.MsgSendMessageDTO;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.event.MessageSendEvent;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.transaction.service.MQProducer;
import com.meteor.chat.websocket.adapter.WSAdapter;
import com.meteor.chat.websocket.service.WebSocketService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessageSendEventListener {

    @Resource
    private MessageDao messageDao;

    @Resource
    private RoomCache roomCache;

    @Resource
    private RoomDao roomDao;

    @Resource
    private HotRoomCache hotRoomCache;

    @Resource
    private WebSocketService webSocketService;

    @Resource
    private MQProducer mqProducer;

    @EventListener(value = MessageSendEvent.class)
    public void pushMsg(MessageSendEvent event) {
        Long msgId = event.getMsgId();
        mqProducer.sendSecureMsg(MQConstant.SEND_MSG_TOPIC, new MsgSendMessageDTO(msgId), msgId);
    }
}
