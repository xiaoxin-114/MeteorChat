package com.meteor.chat.msg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.meteor.chat")
@MapperScan("com.meteor.chat.msg.mapper")
public class MsgApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsgApplication.class, args);
    }
}
