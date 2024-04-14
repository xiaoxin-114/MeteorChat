package com.meteor.chat.common.config;

import com.meteor.chat.common.thread.MyThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ThreadConfig implements AsyncConfigurer {

    public final static String CHAT_EXECUTOR = "meteorChatExecutor";

    public final static String WB_EXECUTRO = "websocketExecutor";

    @Override
    public Executor getAsyncExecutor() {
        return chatExecutor();
    }

    @Bean(name = CHAT_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor chatExecutor(){
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        poolTaskExecutor.setCorePoolSize(10);
        poolTaskExecutor.setMaxPoolSize(10);
        poolTaskExecutor.setQueueCapacity(200);
        poolTaskExecutor.setThreadNamePrefix("meteorChat-");
        //设置拒绝策略，满了调用线程执行，认为重要任务
        poolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        poolTaskExecutor.setThreadFactory(new MyThreadFactory(poolTaskExecutor));
        poolTaskExecutor.initialize();
        return poolTaskExecutor;
    }

    @Bean(name = WB_EXECUTRO)
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
