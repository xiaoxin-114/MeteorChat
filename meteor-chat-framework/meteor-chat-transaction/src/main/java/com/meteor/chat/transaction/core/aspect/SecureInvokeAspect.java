package com.meteor.chat.transaction.core.aspect;

import cn.hutool.core.date.DateUtil;
import com.meteor.chat.transaction.core.annotation.SecureInvoke;
import com.meteor.chat.transaction.core.domian.SecureInvokeDTO;
import com.meteor.chat.transaction.core.domian.SecureInvokeRecord;
import com.meteor.chat.transaction.core.service.SecureInvokeService;
import com.meteor.chat.common.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Aspect
@Slf4j
public class SecureInvokeAspect {

    public static final double RETRY_INTERVAL_MINUTES = 2D;

    private final SecureInvokeService secureInvokeService;

    @Around("@annotation(secureInvoke)")
    public Object secureInvoke(ProceedingJoinPoint joinPoint, SecureInvoke secureInvoke) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        // 如果不在事务中，则直接执行原始方法，不需要进行分布式
        if (!inTransaction) {
            return joinPoint.proceed();
        }
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = methodSignature.getMethod().getName();
        List<String> parametersTypeName = Arrays.stream(methodSignature.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        SecureInvokeDTO secureInvokeDTO = SecureInvokeDTO.builder()
                .className(className)
                .methodName(methodName)
                .parameterTypes(JsonUtils.toStr(parametersTypeName))
                .args(JsonUtils.toStr(args))
                .build();
        SecureInvokeRecord secureInvokeRecord = SecureInvokeRecord.builder()
                .maxRetryTimes(secureInvoke.maxRetryTimes())
                .nextRetryTime(DateUtil.offsetMinute(new Date(), (int) RETRY_INTERVAL_MINUTES))
                .secureInvokeDTO(secureInvokeDTO)
                .status(SecureInvokeRecord.STATUS_WAIT)
                .build();
        secureInvokeService.invoke(secureInvokeRecord, secureInvoke.async());
        return null;
    }
}
