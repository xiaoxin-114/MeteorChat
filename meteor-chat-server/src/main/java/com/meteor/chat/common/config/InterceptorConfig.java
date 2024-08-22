package com.meteor.chat.common.config;

import com.meteor.chat.common.interceptor.BlackInterceptor;
import com.meteor.chat.common.interceptor.CorsInterceptor;
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
    @Resource
    private CorsInterceptor corsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/capi/**");

        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/capi/**").order(-2);

        registry.addInterceptor(blackInterceptor)
                .addPathPatterns("/capi/**");

        registry.addInterceptor(corsInterceptor)
                .addPathPatterns("/**");
    }
}
