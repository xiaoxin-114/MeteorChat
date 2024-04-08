package com.meteor.chat.consumer;

import com.meteor.chat.common.constants.MQConstant;
import com.meteor.chat.common.domain.dto.LoginMessageDTO;
import com.meteor.chat.websocket.service.WebSocketService;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@RocketMQMessageListener(consumerGroup = MQConstant.LOGIN_MSG_GROUP, topic = MQConstant.LOGIN_MSG_TOPIC, messageModel = MessageModel.BROADCASTING)
@Component
public class LoginSuccessConsumer implements RocketMQListener<LoginMessageDTO> {

    @Resource
    private WebSocketService webSocketService;


    @Override
    public void onMessage(LoginMessageDTO loginMessageDTO) {
        webSocketService.scanLoginSuccess(loginMessageDTO.getCode(), loginMessageDTO.getUid());
    }
}
