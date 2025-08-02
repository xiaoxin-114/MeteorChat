package com.meteor.chat.gateway.filter;

import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.constants.MDCKey;
import com.meteor.chat.common.utils.TraceIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Component
public class TraceIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = TraceIdUtils.generateTraceId();
        exchange.getRequest().getHeaders()
                .put(CommonConstants.TRACE_ID_KEY, Collections.singletonList(traceId));
        try {
            MDC.put(MDCKey.TID, traceId);
            return chain.filter(exchange);
        } finally {
            MDC.remove(MDCKey.TID);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
