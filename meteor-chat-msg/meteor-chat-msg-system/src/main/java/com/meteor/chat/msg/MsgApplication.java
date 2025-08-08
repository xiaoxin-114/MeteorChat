package com.meteor.chat.msg;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
@DubboComponentScan("com.meteor.chat")
@MapperScan("com.meteor.chat.msg.mapper")
public class MsgApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsgApplication.class, args);
    }
}
