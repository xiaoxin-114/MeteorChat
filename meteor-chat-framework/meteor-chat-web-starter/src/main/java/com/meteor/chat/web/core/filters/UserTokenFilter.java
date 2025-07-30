package com.meteor.chat.web.core.filters;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.meteor.chat.common.constants.MDCKey;
import com.meteor.chat.api.UserLoginApi;
import com.meteor.chat.web.core.context.UserContext;
import com.meteor.chat.web.core.domian.RequestInfo;
import com.meteor.chat.web.core.enums.HttpErrorEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * 关于token验证的拦截器
 */
@Slf4j
@RequiredArgsConstructor
public class UserTokenFilter extends OncePerRequestFilter {

    public static final String ATTRIBUTE_UID = "uid";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 看清楚，空格必须加上，否则token解析错误
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";

    private final UserLoginApi userLoginApi;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        if (StrUtil.isNotBlank(token)) {
            Long uid = userLoginApi.validToken(token);
            //如果token没过期，用户已经登入，把uid存入request中
            if (uid != null) {
                request.setAttribute(ATTRIBUTE_UID, uid);
                String ip = ServletUtil.getClientIP(request);
                RequestInfo info = new RequestInfo();
                info.setIp(ip);
                info.setUid(uid);
                UserContext.set(info);
                MDC.put(MDCKey.UID, String.valueOf(uid));
            }
        }
        //如果没有登入，且访问的地址非公共域，那么直接返回401，并拦截该请求
        if (!isPublic(request)){
            HttpErrorEnum.ACCESS_DENIED.sendErrorResponse(response);
            return;
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(ATTRIBUTE_UID);
            UserContext.remove();
        }
    }

    /**
     * 从请求中获取到token
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request){
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        return Optional.ofNullable(authorization)
                .filter(header -> header.startsWith(AUTHORIZATION_SCHEMA))
                .map(header -> header.substring(AUTHORIZATION_SCHEMA.length()))
                .orElse(null);
    }

    /**
     * 判断请求访问的地址是否需要登录
     * @param request
     * @return true表示不需要，false需要
     */
    private boolean isPublic(HttpServletRequest request){
        String uri = request.getRequestURI();
        String[] strings = uri.split("/");
        return strings.length > 2 && "public".equals(strings[3]);
    }

}
