package com.meteor.chat.rabbitmq.core.producer;

import cn.hutool.core.util.StrUtil;
import com.meteor.chat.rabbitmq.core.constants.MQConstant;
import com.meteor.chat.transaction.core.annotation.SecureInvoke;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
public class MQProducer {

    private final RabbitTemplate mqTemplate;
    @Value("${push.instanceId:01}")
    private String instanceId;

    public String singleQueueName;

    public String roomQueueName;

    @PostConstruct
    public void init() {
        singleQueueName = MQConstant.SINGLE_PUSH_QUEUE.replace("${instanceId}", instanceId);
        roomQueueName = MQConstant.ROOM_PUSH_QUEUE.replace("${instanceId}", instanceId);
    }

    public void sendMsg(String exchange, String routingKey, Object body) {
        if (StrUtil.isNotBlank(routingKey)) {
            routingKey = routingKey.contains("${instanceId") ? routingKey.replace("${instanceId}", instanceId) : routingKey;
        }
        mqTemplate.convertAndSend(exchange, routingKey,  body);
    }

    /**
     * 发送可靠消息，在事务提交后保证发送成功
     *
     * @param body
     */
    @SecureInvoke
    public void sendSecureMsg(String exchange, String routingKey, Object body) {
        if (StrUtil.isNotBlank(routingKey)) {
            routingKey = routingKey.contains("${instanceId") ? routingKey.replace("${instanceId}", instanceId) : routingKey;
        }
        mqTemplate.convertAndSend(exchange, routingKey, body);
    }

    public String getSingleQueueName() {
        return singleQueueName;
    }

    public String getRoomQueueName() {
        return roomQueueName;
    }
}

