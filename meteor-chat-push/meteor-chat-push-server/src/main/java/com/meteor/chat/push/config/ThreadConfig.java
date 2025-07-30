package com.meteor.chat.push.config;

import com.meteor.chat.common.thread.MyThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadConfig {
    public final static String WB_EXECUTOR = "websocketExecutor";

    @Bean(name = WB_EXECUTOR)
    public ThreadPoolTaskExecutor websocketExecutor(){
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        poolTaskExecutor.setCorePoolSize(16);
        poolTaskExecutor.setMaxPoolSize(16);
        //支持同时推送1000人
        poolTaskExecutor.setQueueCapacity(1000);
        //满了直接丢弃，默认为不重要消息推送
        poolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        poolTaskExecutor.setThreadNamePrefix("websocket-");
        poolTaskExecutor.setThreadFactory(new MyThreadFactory(poolTaskExecutor));
        poolTaskExecutor.initialize();
        return poolTaskExecutor;
    }
}
