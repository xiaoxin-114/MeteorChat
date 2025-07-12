package com.meteor.chat.rabbitmq.config;

import com.meteor.chat.rabbitmq.producer.MQProducer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.meteor.chat.rabbitmq.constants.MQConstant;

/**
 * @author meteor
 */
@Configuration
public class RabbitMqAutoConfiguration {

    @Bean
    public Exchange sendMsgExchange() {
        return ExchangeBuilder.directExchange(MQConstant.SEND_MSG_EXCHANGE).build();
    }

    @Bean
    public Exchange pushExchange() {
        return ExchangeBuilder.topicExchange(MQConstant.PUSH_EXCHANGE).build();
    }

    @Bean
    public Exchange scanExchange() {
        return ExchangeBuilder.topicExchange(MQConstant.SCAN_EXCHANGE).build();
    }

    @Bean
    public Exchange loginExchange() {
        return ExchangeBuilder.topicExchange(MQConstant.LOGIN_EXCHANGE).build();
    }

    @Bean
    public Queue sendMsgQueue() {
        return new Queue(MQConstant.SEND_MSG_QUEUE);
    }

    @Bean
    public Queue pushQueue() {
        return new Queue(MQConstant.PUSH_QUEUE);
    }

    @Bean
    public Queue loginQueue() {
        return new Queue(MQConstant.LOGIN_QUEUE);
    }

    @Bean
    public Queue scanQueue() {
        return new Queue(MQConstant.SCAN_QUEUE);
    }


    /**
     * sendMsgExchange -> sendMsgQueue (DirectExchange)
     * 使用固定 routing key
     */
    @Bean
    public Binding bindingSendMsg(Exchange sendMsgExchange, Queue sendMsgQueue) {
        return BindingBuilder.bind(sendMsgQueue).to(sendMsgExchange).with(MQConstant.SEND_MSG_ROUTING_KEY).noargs();
    }

    /**
     * pushExchange -> pushQueue (TopicExchange)
     * 支持模糊匹配 routing key
     */
    @Bean
    public Binding bindingPushMsg(Exchange pushExchange, Queue pushQueue) {
        return BindingBuilder.bind(pushQueue).to(pushExchange).with(MQConstant.PUSH_ROUTING_KEY).noargs();
    }

    /**
     * scanExchange -> scanQueue (TopicExchange)
     * 用于扫码相关事件
     */
    @Bean
    public Binding bindingScanEvent(Exchange scanExchange, Queue scanQueue) {
        return BindingBuilder.bind(scanQueue).to(scanExchange).with(MQConstant.SCAN_ROUTING_KEY).noargs();
    }

    /**
     * loginExchange -> loginQueue (TopicExchange)
     * 用户登录事件
     */
    @Bean
    public Binding bindingLoginEvent(Exchange loginExchange, Queue loginQueue) {
        return BindingBuilder.bind(loginQueue).to(loginExchange).with(MQConstant.LOGIN_ROUTING_KEY).noargs();
    }

    @Bean
    public MQProducer mqProducer(RabbitTemplate template) {
        return new MQProducer(template);
    }
}
