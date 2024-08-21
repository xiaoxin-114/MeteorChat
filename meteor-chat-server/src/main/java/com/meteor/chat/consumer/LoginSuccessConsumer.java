package com.meteor.chat.consumer;

import com.meteor.chat.common.constants.MQConstant;
import com.meteor.chat.common.domain.dto.LoginMessageDTO;
import com.meteor.chat.websocket.service.WebSocketService;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LoginSuccessConsumer extends AbstractConsumer<LoginMessageDTO> {

    @Resource
    private WebSocketService webSocketService;

    @Override
    public void consume(LoginMessageDTO loginMessageDTO) {
        webSocketService.scanLoginSuccess(loginMessageDTO.getCode(), loginMessageDTO.getUid());
    }

    @Override
    public String getKey() {
        return MQConstant.LOGIN_MSG_TOPIC;
    }
}
