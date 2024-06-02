package com.meteor.chat.common.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(org.springframework.test.context.junit4.SpringRunner.class)
public class UtilTest {
    @Resource
    private JWTUtils jwtUtils;

    @Test
    public void getToken() {
        String token = jwtUtils.createToken(10L);
        System.out.println(token);
    }
}
