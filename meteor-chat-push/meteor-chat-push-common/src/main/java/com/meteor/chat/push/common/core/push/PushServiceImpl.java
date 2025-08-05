package com.meteor.chat.push.common.core.push;

import com.meteor.chat.push.common.domain.dto.PushMessageDTO;
import com.meteor.chat.push.common.domain.vo.WSBaseResp;
import com.meteor.chat.push.common.mq.SinglePushMQProducer;
import com.meteor.chat.rabbitmq.core.constants.MQConstant;
import com.meteor.chat.rabbitmq.core.producer.MQProducer;
import lombok.RequiredArgsConstructor;
import java.util.List;

// todo 后续推送消息支持多种方式（mq，kafka，redis）
@RequiredArgsConstructor
public class PushServiceImpl implements PushService {

    private final MQProducer mqProducer;
    private final SinglePushMQProducer singlePushMQProducer;

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
        singlePushMQProducer.sendSingleMsg(MQConstant.SINGLE_PUSH_EXCHANGE, MQConstant.SINGLE_PUSH_ROUTING_KEY, new PushMessageDTO(msg, uid), uid);
    }
}
