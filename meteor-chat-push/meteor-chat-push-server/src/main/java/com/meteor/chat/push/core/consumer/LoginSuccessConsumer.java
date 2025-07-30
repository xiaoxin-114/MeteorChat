package com.meteor.chat.push.core.consumer;

import com.meteor.chat.push.common.domain.dto.LoginMessageDTO;
import com.meteor.chat.push.core.service.WebSocketService;
import com.meteor.chat.rabbitmq.core.constants.MQConstant;
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
