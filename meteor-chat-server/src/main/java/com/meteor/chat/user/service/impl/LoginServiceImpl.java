package com.meteor.chat.user.service.impl;

import com.meteor.chat.common.constants.RedisKey;
import com.meteor.chat.common.util.JWTUtils;
import com.meteor.chat.common.util.RedisUtils;
import com.meteor.chat.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    private static final Long EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7; // 过期时间一周

    @Override
    public boolean verify(String token) {
        Long uid = JWTUtils.getUid(token);
        if (Objects.isNull(uid)) {
            return false;
        }
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        String realToken = RedisUtils.getStr(key);
        return realToken != null && realToken.equals(token);
    }

    @Override
    public void renewalTokenIfNecessary(String token) {

    }

    @Override
    public String login(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_TOKEN_STRING, uid);
        String token = RedisUtils.getStr(key);
        if (StringUtils.isNotEmpty(token)){
            return token;
        }
        token = JWTUtils.createToken(uid);
        RedisUtils.set(key, token, EXPIRE_TIME);
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        boolean verify = verify(token);
        return verify ? JWTUtils.getUid(token) : null;
    }
}
