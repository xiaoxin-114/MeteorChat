package com.meteor.chat.room;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDubbo
@DubboComponentScan(basePackages = "com.meteor.chat")
@MapperScan("com.meteor.chat.room.mapper")
public class RoomApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoomApplication.class, args);
    }
}
