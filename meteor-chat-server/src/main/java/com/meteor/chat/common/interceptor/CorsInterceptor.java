package com.meteor.chat.common.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Method", "'GET, POST, OPTIONS, DELETE, PUT'");
        response.addHeader("Access-Control-Allow-Header", "'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization'");
        response.addHeader("Access-Control-Max-Age", "1728000");
        if (request.getMethod().equals("OPTION")) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return false;
        }
        return true;
    }
}
