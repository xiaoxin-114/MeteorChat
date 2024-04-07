package com.meteor.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.meteor.chat.common.mapper")
public class MeteorChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeteorChatApplication.class, args);
    }
}
