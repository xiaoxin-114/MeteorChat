package com.meteor.chat.rabbitmq.producer;

import com.meteor.chat.transaction.annotation.SecureInvoke;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
@AllArgsConstructor
public class MQProducer {

    private RabbitTemplate rocketMQTemplate;

    public void sendMsg(String exchange, String routingKey, Object body) {

        rocketMQTemplate.convertAndSend(exchange, routingKey,  body);
    }

    /**
     * 发送可靠消息，在事务提交后保证发送成功
     *
     * @param body
     */
    @SecureInvoke
    public void sendSecureMsg(String exchange, String routingKey, Object body) {
        rocketMQTemplate.convertAndSend(exchange, routingKey, body);
    }
}

