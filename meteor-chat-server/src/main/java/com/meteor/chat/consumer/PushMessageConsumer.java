package com.meteor.chat.consumer;

import com.meteor.chat.common.domain.dto.PushMessageDTO;
import com.meteor.chat.rabbitmq.constants.MQConstant;
import com.meteor.chat.websocket.service.WebSocketService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.util.CollectionUtils;
import org.junit.Assert;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class PushMessageConsumer {

    @Resource
    private WebSocketService webSocketService;

    @RabbitListener(queues = {"#{@mqProducer.getSingleQueueName()}", "#{@mqProducer.getRoomQueueName()}"})
    public void consume(PushMessageDTO pushMessageDTO) {
        if (PushMessageDTO.ALL.equals(pushMessageDTO.getType())) {
            webSocketService.sendToAllOnline(pushMessageDTO.getWsBaseResp());
        } else {
            List<Long> uidList = pushMessageDTO.getUidList();
            Assert.assertTrue("消息异常，推送消息无用户id", !CollectionUtils.isEmpty(uidList));
            // 过滤掉当前服务器不存在的用户
            uidList = uidList.stream().filter(uid -> webSocketService.haveUid(uid)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(uidList)) {
                uidList.forEach(uid -> webSocketService.sendToUid(pushMessageDTO.getWsBaseResp(), uid));
            }
        }
    }
}
