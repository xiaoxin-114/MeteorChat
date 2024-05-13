package com.meteor.chat.common.interceptor;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.meteor.chat.common.domain.RequestInfo;
import com.meteor.chat.common.domain.enums.BlackTypeEnum;
import com.meteor.chat.common.domain.enums.HttpErrorEnum;
import com.meteor.chat.common.util.UserContext;
import com.meteor.chat.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class BlackInterceptor implements HandlerInterceptor {

    @Resource
    private UserCache userCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<Integer, Set<String>> blackMap = userCache.getBlackMap();
        RequestInfo user = UserContext.get();
        if (inBlackList(user.getUid(), blackMap.get(BlackTypeEnum.UID.getId()))
            || inBlackList(user.getIp(), blackMap.get(BlackTypeEnum.IP.getId()))) {
            HttpErrorEnum.ACCESS_DENIED.sendErrorResponse(response);
            return false;
        }
        return true;
    }

    private boolean inBlackList(Object target, Set<String> strings) {
        if (Objects.isNull(target) || CollectionUtils.isEmpty(strings)) {
            return false;
        }
        return strings.contains(target.toString());
    }
}
