package com.meteor.chat.rabbitmq.config;

import com.meteor.chat.rabbitmq.producer.MQProducer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.meteor.chat.rabbitmq.constants.MQConstant;

/**
 * @author meteor
 */
@Configuration
public class RabbitMqAutoConfiguration {

    @Value("${push.instanceId")
    private String instanceId;

    private final MQProducer mqProducer;

    public RabbitMqAutoConfiguration(RabbitTemplate template) {
        // 指定rabbitmq的序列化方式
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        this.mqProducer = new MQProducer(template);
    }

    @Bean("mqProducer")
    public MQProducer mqProducer() {
        return mqProducer;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 临时设置 MessageConverter 为 Jackson2JsonMessageConverter
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
    /**
     * 都使用topic交换机，便于性能扩展，将大队列分成多个小队列
     * @return
     */
    @Bean
    public Exchange sendMsgExchange() {
        return ExchangeBuilder.topicExchange(MQConstant.SEND_MSG_EXCHANGE).build();
    }

    @Bean
    public Exchange singlePushExchange() {
        return ExchangeBuilder.topicExchange(MQConstant.SINGLE_PUSH_EXCHANGE).build();
    }

    @Bean
    public FanoutExchange roomPushExchange() {
        return ExchangeBuilder.fanoutExchange(MQConstant.ROOM_PUSH_EXCHANGE).build();
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
    public Queue singlePushQueue() {
        return new Queue(mqProducer.getSingleQueueName());
    }

    @Bean
    public Queue roomPushQueue() {
        return new Queue(mqProducer.getRoomQueueName());
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
    public Binding bindingSinglePushMsg(Exchange singlePushExchange, Queue singlePushQueue) {
        return BindingBuilder.bind(singlePushQueue).to(singlePushExchange).with(MQConstant.SINGLE_PUSH_ROUTING_KEY.replace("${instanceId}", instanceId)).noargs();
    }

    /**
     * pushExchange -> pushQueue (TopicExchange)
     * 支持模糊匹配 routing key
     */
    @Bean
    public Binding bindingRoomPushMsg(FanoutExchange roomPushExchange, Queue roomPushQueue) {
        return BindingBuilder.bind(roomPushQueue).to(roomPushExchange);
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


}
