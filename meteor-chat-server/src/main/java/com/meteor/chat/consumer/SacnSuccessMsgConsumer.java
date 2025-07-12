package com.meteor.chat.consumer;

import com.meteor.chat.rabbitmq.constants.MQConstant;
import com.meteor.chat.common.domain.dto.ScanSuccessMessageDTO;
import com.meteor.chat.websocket.service.WebSocketService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 登入二维码扫码成功的消息消费者
 */
@Component
public class SacnSuccessMsgConsumer {
    @Resource
    private WebSocketService webSocketService;

    @RabbitListener(queues = MQConstant.SCAN_QUEUE)
    public void consume(ScanSuccessMessageDTO scanSuccessMessageDTO) {
        webSocketService.scanSuccess(scanSuccessMessageDTO.getCode());
    }
}
