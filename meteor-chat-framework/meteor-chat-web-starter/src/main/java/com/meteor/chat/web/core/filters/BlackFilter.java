package com.meteor.chat.web.core.filters;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.meteor.chat.api.UserInfoApi;
import com.meteor.chat.web.core.context.UserContext;
import com.meteor.chat.web.core.domian.RequestInfo;
import com.meteor.chat.web.core.enums.BlackTypeEnum;
import com.meteor.chat.web.core.enums.HttpErrorEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
@RequiredArgsConstructor
@Slf4j
public class BlackFilter extends OncePerRequestFilter {

    private final UserInfoApi userInfoApi;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<Integer, Set<String>> blackMap = userInfoApi.getBlackMap().getCheckData();
        RequestInfo user = UserContext.get();
        if (Objects.nonNull(user) && (inBlackList(user.getUid(), blackMap.get(BlackTypeEnum.UID.getId()))
                || inBlackList(user.getIp(), blackMap.get(BlackTypeEnum.IP.getId())))) {
            HttpErrorEnum.ACCESS_DENIED.sendErrorResponse(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean inBlackList(Object target, Set<String> strings) {
        if (Objects.isNull(target) || CollectionUtils.isEmpty(strings)) {
            return false;
        }
        return strings.contains(target.toString());
    }
}
