package com.meteor.chat.common.config;

import com.meteor.chat.common.interceptor.BlackInterceptor;
import com.meteor.chat.common.interceptor.TokenInterceptor;
import com.meteor.chat.common.interceptor.UserContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private TokenInterceptor tokenInterceptor;
    @Resource
    private BlackInterceptor blackInterceptor;
    @Resource
    private UserContextInterceptor userContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/capi/**");

        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/capi/**");

        registry.addInterceptor(blackInterceptor)
                .addPathPatterns("/capi/**");
    }
}
