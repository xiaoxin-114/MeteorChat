package com.meteor.chat.push.core.consumer;

import com.meteor.chat.push.common.domain.dto.ScanSuccessMessageDTO;
import com.meteor.chat.push.core.service.WebSocketService;
import com.meteor.chat.rabbitmq.core.constants.MQConstant;
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
