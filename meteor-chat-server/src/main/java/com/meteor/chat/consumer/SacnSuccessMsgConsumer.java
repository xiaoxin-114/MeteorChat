package com.meteor.chat.consumer;

import com.meteor.chat.common.constants.MQConstant;
import com.meteor.chat.common.domain.dto.ScanSuccessMessageDTO;
import com.meteor.chat.websocket.service.WebSocketService;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 登入二维码扫码成功的消息消费者
 */
@RocketMQMessageListener(topic = MQConstant.SCAN_MSG_TOPIC, consumerGroup = MQConstant.SCAN_MSG_GROUP, messageModel = MessageModel.BROADCASTING)
@Component
public class SacnSuccessMsgConsumer implements RocketMQListener<ScanSuccessMessageDTO> {
    @Resource
    private WebSocketService webSocketService;


    @Override
    public void onMessage(ScanSuccessMessageDTO scanSuccessMessageDTO) {
        webSocketService.scanSuccess(scanSuccessMessageDTO.getCode());
    }
}
