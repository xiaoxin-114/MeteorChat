package com.meteor.chat.rabbitmq.core.producer;

import com.meteor.chat.rabbitmq.core.constants.MQConstant;
import com.meteor.chat.transaction.core.annotation.SecureInvoke;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;


@RequiredArgsConstructor
public class MQProducer {

    private final RabbitTemplate mqTemplate;
    @Value("${push.instanceId:01}")
    private String instanceId;

    private String msgPushQueueName;
    private String loginQueueName;
    private String scanQueueName;

    @PostConstruct
    public void init() {
        msgPushQueueName = MQConstant.MSG_PUSH_QUEUE.replace(MQConstant.INSTANCE_ID_PLACE, instanceId);
        loginQueueName = MQConstant.LOGIN_QUEUE.replace(MQConstant.INSTANCE_ID_PLACE, instanceId);
        scanQueueName = MQConstant.SCAN_QUEUE.replace(MQConstant.INSTANCE_ID_PLACE, instanceId);
    }

    public void sendMsg(String exchange, String routingKey, Object body) {
        mqTemplate.convertAndSend(exchange, routingKey,  body);
    }

    /**
     * 发送可靠消息，在事务提交后保证发送成功
     *
     * @param body
     */
    @SecureInvoke
    public void sendSecureMsg(String exchange, String routingKey, Object body) {
        mqTemplate.convertAndSend(exchange, routingKey, body);
    }

    public String getMsgPushQueueName() {
        return msgPushQueueName;
    }

    public String getLoginQueueName() {
        return loginQueueName;
    }

    public String getScanQueueName() {
        return scanQueueName;
    }

    public String getInstanceId() {
        return instanceId;
    }
}

