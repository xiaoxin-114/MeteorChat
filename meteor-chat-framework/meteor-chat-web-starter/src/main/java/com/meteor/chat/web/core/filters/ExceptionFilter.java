package com.meteor.chat.web.core.filters;

import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.web.core.exception.GlobalExceptionHandler;
import com.meteor.chat.web.core.utils.WebFrameworkUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {

    private final GlobalExceptionHandler globalExceptionHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Throwable e) {
            ApiResult<Void> apiResult = globalExceptionHandler.globalExceptionHandler(e);
            WebFrameworkUtils.sendErrorMsg(response, apiResult);
        }
    }
}
