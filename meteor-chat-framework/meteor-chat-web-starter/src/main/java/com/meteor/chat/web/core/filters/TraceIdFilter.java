package com.meteor.chat.web.core.filters;


import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.constants.MDCKey;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TraceIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceId = request.getHeader(CommonConstants.TRACE_ID_KEY);
        try {
            MDC.put(MDCKey.TID, traceId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDCKey.TID);
        }
    }
}
