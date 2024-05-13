package com.meteor.chat.common.aspect;

import cn.hutool.extra.spring.SpringUtil;
import com.meteor.chat.common.annotation.RedissonLock;
import com.meteor.chat.common.util.LockUtil;
import com.meteor.chat.common.util.SpElUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.checkerframework.checker.units.qual.A;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Aspect
@Order(0)//确保比事务注解先执行，分布式锁在事务外
/**
 * 分布式锁切面
 */
public class RedissonLockAspect {

    @Resource
    private LockUtil lockUtil;

    @Pointcut("@annotation(com.meteor.chat.common.annotation.RedissonLock)")
    public void methodRedissonLockPointcut() {}

    @Pointcut("@within(com.meteor.chat.common.annotation.RedissonLock)")
    public void typeRedissonLockPointcut() {}

    @Around("methodRedissonLockPointcut()")
    public Object methodAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return doAround(joinPoint);
    }

    @Around("typeRedissonLockPointcut()")
    public Object typeAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return doAround(joinPoint);
    }

    private Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            RedissonLock annotation = method.getAnnotation(RedissonLock.class);
            String key = annotation.key();
            Object[] args = joinPoint.getArgs();
            String prefixKey = annotation.prefixKey();
            if (StringUtils.isEmpty(prefixKey)) {
                prefixKey = getDefaultPrefix(method);
            }
            key = SpElUtils.parseSpEl(method, key, args);
            long time = annotation.time();
            TimeUnit timeUnit = annotation.timeUnit();
            return lockUtil.executeWithLockWithThrows(prefixKey + ":" + key, time, timeUnit, joinPoint::proceed);
        }
        return null;
    }

    private static String getDefaultPrefix(Method method) {
        return method.getDeclaringClass() + "#" + method.getName();
    }

}
