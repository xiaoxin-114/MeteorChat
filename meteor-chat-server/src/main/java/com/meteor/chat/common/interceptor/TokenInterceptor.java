package com.meteor.chat.common.interceptor;

import com.meteor.chat.common.constants.MDCKey;
import com.meteor.chat.common.domain.enums.HttpErrorEnum;
import com.meteor.chat.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 关于token验证的拦截器
 */
@Component
@Slf4j
@Order(-2)
public class TokenInterceptor implements HandlerInterceptor {

    public static final String ATTRIBUTE_UID = "uid";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer";

    @Resource
    private LoginService loginService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long uid = loginService.getValidUid(token);
        //如果token没过期，用户已经登入，把uid存入request中
        if (uid != null){
            request.setAttribute(ATTRIBUTE_UID, uid);
        }else {
            //如果没有登入，且访问的地址非公共域，那么直接返回401，并拦截该请求
            if (!isPublic(request)){
                HttpErrorEnum.ACCESS_DENIED.sendErrorResponse(response);
                return false;
            }
        }
        MDC.put(MDCKey.UID, String.valueOf(uid));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(ATTRIBUTE_UID);
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
