package com.meteor.chat.consumer;

import com.meteor.chat.common.constants.MQConstant;
import com.meteor.chat.common.domain.dto.PushMessageDTO;
import com.meteor.chat.websocket.service.WebSocketService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RocketMQMessageListener(consumerGroup = MQConstant.PUSH_GROUP, topic = MQConstant.PUSH_TOPIC, messageModel = MessageModel.BROADCASTING)
public class PushMessageConsumer implements RocketMQListener<PushMessageDTO> {

    @Resource
    private WebSocketService webSocketService;

    @Override
    public void onMessage(PushMessageDTO pushMessageDTO) {
        List<Long> uidList = pushMessageDTO.getUidList();
        // 过滤掉当前服务器不存在的用户
        uidList = uidList.stream().filter(uid -> webSocketService.haveUid(uid)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(uidList)) {
            uidList.forEach(uid -> webSocketService.sendToUid(pushMessageDTO.getWsBaseResp(), uid));
        }
    }
}
