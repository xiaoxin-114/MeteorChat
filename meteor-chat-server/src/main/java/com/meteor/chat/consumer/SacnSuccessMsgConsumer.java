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
@Component
public class SacnSuccessMsgConsumer extends AbstractConsumer<ScanSuccessMessageDTO> {
    @Resource
    private WebSocketService webSocketService;

    @Override
    public void consume(ScanSuccessMessageDTO scanSuccessMessageDTO) {
        webSocketService.scanSuccess(scanSuccessMessageDTO.getCode());
    }

    @Override
    public String getKey() {
        return MQConstant.SCAN_MSG_GROUP;
    }
}
