package com.meteor.chat.route.service.impl;

import com.meteor.chat.common.domain.dto.PushMessageDTO;
import com.meteor.chat.rabbitmq.constants.MQConstant;
import com.meteor.chat.rabbitmq.producer.MQProducer;
import com.meteor.chat.route.service.PushService;
import com.meteor.chat.websocket.domain.vo.WSBaseResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PushServiceImpl implements PushService {

    @Resource
    private MQProducer mqProducer;

    @Override
    public void pushRoomMsg(WSBaseResp<?> msg, List<Long> uidList) {
        if (uidList.size() == 1) {
            pushSingleMsg(msg, uidList.get(0));
        } else {
            mqProducer.sendMsg(MQConstant.ROOM_PUSH_EXCHANGE, null, new PushMessageDTO(msg, uidList, PushMessageDTO.NOT_ALL));
        }
    }

    @Override
    public void pushRoomMsg(WSBaseResp<?> msg) {
        mqProducer.sendMsg(MQConstant.ROOM_PUSH_EXCHANGE, null, new PushMessageDTO(msg));
    }

    @Override
    public void pushSingleMsg(WSBaseResp<?> msg, Long uid) {
        // todo 后续优化，同步websocket模块一起，websocket建立连接时，就要根据uid分配给不同的用户
        // 但是前端建立websocket连接时，还没有用户信息，这个无法确定
        mqProducer.sendMsg(MQConstant.SINGLE_PUSH_EXCHANGE, MQConstant.SINGLE_PUSH_ROUTING_KEY, new PushMessageDTO(msg, uid));
    }
}
