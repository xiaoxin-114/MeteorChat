package com.meteor.chat.gateway.filter;

import com.meteor.chat.common.constants.MDCKey;
import com.meteor.chat.common.exception.CommonErrorEnum;
import com.meteor.chat.api.UserLoginApi;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.gateway.util.WebFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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

    private final WebClient webClient;

    public UserTokenFilter(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        // Q：为什么不使用 OAuth2TokenApi 进行调用？
        // A1：Spring Cloud OpenFeign 官方未内置 Reactive 的支持 https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/#reactive-support
        // A2：校验 Token 的 API 需要使用到 header[tenant-id] 传递租户编号，暂时不想编写 RequestInterceptor 实现
        // 因此，这里采用 WebClient，通过 lbFunction 实现负载均衡
        this.webClient = WebClient.builder().filter(lbFunction).build();
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = getToken(request);
        if (token != null) {
            return checkToken(token)
                    .flatMap(uid -> {
                        if (uid != null) {
                            try {
                                MDC.put(MDCKey.UID, String.valueOf(uid));
                                return chain.filter(exchange);
                            } finally {
                                MDC.remove(MDCKey.UID);
                            }
                        } else {
                            return handleUnauthorized(exchange, request, chain);
                        }
                    }).onErrorResume(throwable -> {
                        log.error("Token validation error", throwable);
                        return handleUnauthorized(exchange, request, chain);
                    });

        }
        return handleUnauthorized(exchange, request, chain);
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, ServerHttpRequest request, GatewayFilterChain chain) {
        // token校验失败且不是公开接口，返回401错误
        if (!isPublic(request)) {
            ApiResult<?> result = ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR.getErrCode(), "未授权访问");
            return WebFrameworkUtils.writeJSON(exchange, result);
        }

        // 公开接口直接放行
        return chain.filter(exchange);
    }

    /**
     * 使用webclient发送请求校验Token
     */
    private Mono<Long> checkToken(String token) {
        return webClient.get()
                .uri(UserLoginApi.VALID_TOKEN_URI, uriBuilder -> uriBuilder.queryParam("token", token).build())
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_SCHEMA + token)
                .retrieve()
                .bodyToMono(Long.class);
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
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}