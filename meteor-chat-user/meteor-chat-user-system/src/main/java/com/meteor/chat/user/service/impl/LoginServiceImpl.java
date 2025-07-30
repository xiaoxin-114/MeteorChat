package com.meteor.chat.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.meteor.chat.api.user.dto.LoginSuccessDTO;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.common.exception.CommonErrorEnum;
import com.meteor.chat.redis.core.constants.RedisKey;
import com.meteor.chat.redis.core.util.RedisUtils;
import com.meteor.chat.user.adapter.UserAdapter;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.dao.UserRoleDao;
import com.meteor.chat.user.domain.entity.User;
import com.meteor.chat.user.domain.entity.UserRole;
import com.meteor.chat.user.enums.UserStatusEnum;
import com.meteor.chat.user.event.UserOfflineEvent;
import com.meteor.chat.user.event.UserOnlineEvent;
import com.meteor.chat.user.service.LoginService;
import com.meteor.chat.user.service.cache.UserCache;
import com.meteor.chat.user.utils.JWTUtils;
import com.meteor.chat.user.utils.PBKDF2Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    private static final Long EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7; // 过期时间一周
    @Resource
    private JWTUtils jwtUtils;
    @Value("${mock.prefix}")
    private String mockPrefix = "test";
    @Value("${mock.enable}")
    private Boolean mockEnable;
    @Resource
    private UserDao userDao;
    @Resource
    private UserRoleDao userRoleDao;
    @Resource
    private UserCache userCache;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public boolean verify(String token) {
        Long uid = jwtUtils.getUid(token);
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
        token = jwtUtils.createToken(uid);
        RedisUtils.set(key, token, EXPIRE_TIME);
        return token;
    }

    @Override
    public Long getValidUid(String token) {
        boolean verify = verify(token);
        if (!verify) {
            return mockUid(token);
        }
        return jwtUtils.getUid(token);
    }

    @Override
    public UserInfoDTO successLogin(LoginSuccessDTO loginSuccessDTO) {
        User userInfo = userCache.getUserInfo(loginSuccessDTO.getUid());
        UserRole role = userRoleDao.getUserRoleByUid(loginSuccessDTO.getUid());
        UserInfoDTO userInfoDTO = UserAdapter.buildUserInfoDTO(userInfo, role);
        if (!userCache.isOnline(loginSuccessDTO.getUid())) {
            userInfo.setLastOptTime(new Date());
            userInfo.refreshIp(loginSuccessDTO.getIP());
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, userInfo));
        }
        return userInfoDTO;
    }


    @Override
    public void offLine(Long uid) {
        User userInfo = userCache.getUserInfo(uid);
        userInfo.setLastOptTime(new Date());
        applicationEventPublisher.publishEvent(new UserOfflineEvent(this, userInfo));
    }

    @Override
    public String loginByPassword(String username, String password) {
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            throw new BusinessException(CommonErrorEnum.USERNAME_OR_PASSWORD_EMPTY);
        }
        // 校验用户名
        User user = userDao.getByUsername(username);
        Assert.assertNotNull(CommonErrorEnum.USER_NOT_EXIST.getErrMsg(), user);
        Assert.assertNotEquals(CommonErrorEnum.INNER_USER_LOGIN.getErrMsg(), user.getStatus(), UserStatusEnum.INNER.getId());
        Assert.assertNotEquals(CommonErrorEnum.USER_IN_BLACK.getErrMsg(), user.getStatus(), UserStatusEnum.BLACK.getId());
        // 密码校验
        Assert.assertTrue(CommonErrorEnum.USERNAME_OR_PASSWORD_ERROR.getErrMsg(), PBKDF2Util.verifyPassword(password, user.getPassword(), user.getSalt()));
        return login(user.getId());
    }

    /**
     * 支持测试时模拟用户行为
     * @param token
     * @return
     */
    private Long mockUid(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        if (!mockEnable || !token.startsWith(mockPrefix)) {
            return null;
        }
        return Long.parseLong(token.substring(mockPrefix.length() + 1));
    }
}
