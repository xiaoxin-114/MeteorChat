package com.meteor.chat.web.config;

import com.meteor.chat.api.UserInfoApi;
import com.meteor.chat.api.UserLoginApi;
import com.meteor.chat.web.core.exception.GlobalExceptionHandler;
import com.meteor.chat.web.core.filters.BlackFilter;
import com.meteor.chat.web.core.filters.CorsFilter;
import com.meteor.chat.web.core.filters.TraceIdFilter;
import com.meteor.chat.web.core.filters.UserTokenFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@Slf4j
public class WebAutoConfiguration {
    @Resource
    private UserLoginApi userLoginApi;

    @Resource
    private UserInfoApi userInfoApi;

    @Bean
    public FilterRegistrationBean<UserTokenFilter> userTokenFilter() {
        FilterRegistrationBean<UserTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UserTokenFilter(userLoginApi));
        registrationBean.addUrlPatterns("/capi/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<BlackFilter> blackFilter() {
        FilterRegistrationBean<BlackFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new BlackFilter(userInfoApi));
        registrationBean.addUrlPatterns("/capi/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CorsFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
        FilterRegistrationBean<TraceIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TraceIdFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(-1);
        return registrationBean;
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
