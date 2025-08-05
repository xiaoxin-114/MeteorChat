package com.meteor.chat.push.common.config;

import com.meteor.chat.push.common.core.push.PushService;
import com.meteor.chat.push.common.core.push.PushServiceImpl;
import com.meteor.chat.push.common.mq.SinglePushMQProducer;
import com.meteor.chat.rabbitmq.core.producer.MQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebsocketPushAutoConfiguration {

    @Bean
    public SinglePushMQProducer singlePushMQProducer(MQProducer mqProducer) {
        return new SinglePushMQProducer(mqProducer);
    }

    @Bean
    public PushService pushService(MQProducer mqProducer, SinglePushMQProducer singlePushMQProducer) {
        return new PushServiceImpl(mqProducer, singlePushMQProducer);
    }


}
