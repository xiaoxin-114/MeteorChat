package com.meteor.chat.consumer;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConsumerExecutor {

    private final ThreadPoolTaskExecutor executor;

    public static Map<String, AbstractConsumer> consumerMap = new ConcurrentHashMap<>();

    public ConsumerExecutor(@Qualifier("meteorChatExecutor") ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    /**
     * 注册消息主题与消费者的映射关系
     * @param key
     * @param consumer
     */
    public static void register(String key, AbstractConsumer consumer) {
        consumerMap.put(key, consumer);
    }

    /**
     * 代替mq，异步消费消息
     * @param key 消息的主题
     * @param t 消息体
     * @param <T>
     */
    public <T> void execute(String key, T t) {
        AbstractConsumer consumer = consumerMap.get(key);
        Assert.assertNotNull("项目异常，当前主题" + key + "未配置消费端", consumer);
        executor.execute(() ->  consumer.consume(t));
    }
}
