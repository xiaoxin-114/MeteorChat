package com.meteor.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan(basePackages = "com.meteor.chat.common.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class MeteorChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeteorChatApplication.class, args);
    }
}
