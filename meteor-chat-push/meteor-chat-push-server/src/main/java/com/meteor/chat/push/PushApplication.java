package com.meteor.chat.push;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDubbo
@DubboComponentScan(basePackages = "com.meteor.chat")
public class PushApplication {
    public static void main(String[] args) {
        SpringApplication.run(PushApplication.class, args);
    }
}
