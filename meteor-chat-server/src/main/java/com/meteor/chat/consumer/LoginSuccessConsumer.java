package com.meteor.chat.consumer;

import com.meteor.chat.common.domain.dto.LoginMessageDTO;
import com.meteor.chat.rabbitmq.constants.MQConstant;
import com.meteor.chat.websocket.service.WebSocketService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LoginSuccessConsumer {

    @Resource
    private WebSocketService webSocketService;

    @RabbitListener(queues = MQConstant.LOGIN_QUEUE)
    public void consume(LoginMessageDTO loginMessageDTO) {
        webSocketService.scanLoginSuccess(loginMessageDTO.getCode(), loginMessageDTO.getUid());
    }
}
