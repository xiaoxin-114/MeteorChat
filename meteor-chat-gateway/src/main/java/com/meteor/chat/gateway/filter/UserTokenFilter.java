package com.meteor.chat.gateway.filter;

import com.meteor.chat.common.exception.CommonErrorEnum;
import com.meteor.chat.api.UserLoginApi;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.gateway.util.WebFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@Component
public class UserTokenFilter implements GlobalFilter, Ordered {

    public static final String ATTRIBUTE_UID = "uid";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 看清楚，空格必须加上，否则token解析错误
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";

    // 注意：UserLoginApi是一个RPC接口，在网关服务中需要通过RPC调用用户服务的实现
    @Resource
    private UserLoginApi userLoginApi;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = getToken(request);
        if (token != null) {
            Long uid = userLoginApi.validToken(token);
            if (uid != null) {
                return chain.filter(exchange);
            }
        }
        
        // token校验失败且不是公开接口，返回401错误
        if (!isPublic(request)) {
            ApiResult<?> result = ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR.getErrCode(), "未授权访问");
            return WebFrameworkUtils.writeJSON(exchange, result);
        }
        
        // 公开接口直接放行
        return chain.filter(exchange);
    }

    /**
     * 从请求中获取到token
     * @param request
     * @return
     */
    private String getToken(ServerHttpRequest request){
        String authorization = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
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
    private boolean isPublic(ServerHttpRequest request){
        String uri = request.getURI().getPath();
        String[] strings = uri.split("/");
        return strings.length > 2 && "public".equals(strings[3]);
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}