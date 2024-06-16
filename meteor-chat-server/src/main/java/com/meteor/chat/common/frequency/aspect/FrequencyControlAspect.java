package com.meteor.chat.common.frequency.aspect;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.meteor.chat.common.frequency.annotation.FrequencyControl;
import com.meteor.chat.common.frequency.dto.FrequencyControlBaseDTO;
import com.meteor.chat.common.util.CommonUtils;
import com.meteor.chat.common.util.FrequencyControlUtils;
import com.meteor.chat.common.util.SpElUtils;
import com.meteor.chat.common.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class FrequencyControlAspect {
    private static final String TOTAL_COUNT_WITH_FIX_TIME_STRATEGY_NAME = "TotalCountWithInFixTime";

    @Pointcut(value = "@annotation(com.meteor.chat.common.frequency.annotation.FrequencyControl)")
    private void pointcut(){}

    @Around("pointcut()")
    public Object around(@NotNull ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            Object[] args = joinPoint.getArgs();
            // 获取注解类
            FrequencyControl[] frequencyControlArray = method.getAnnotationsByType(FrequencyControl.class);
            List<FrequencyControlBaseDTO> dtoList = new ArrayList<>(frequencyControlArray.length);
            for (int i = 0; i < frequencyControlArray.length; i++) {
                FrequencyControl frequency = frequencyControlArray[i];
                FrequencyControl.FrequencyTypeEnum type = frequency.type();
                String prefixKey = frequency.prefixKey();
                if (StringUtils.isEmpty(prefixKey)) {
                    prefixKey = CommonUtils.getDefaultPrefix(method);
                }
                prefixKey = prefixKey + ":index:" + i;
                String key = "";
                switch (type){
                    case IP:
                        key = UserContext.getIp();
                        break;
                    case UID:
                        key = UserContext.getUid().toString();
                        break;
                    case SPEL:
                        key = SpElUtils.parseSpEl(method, frequency.spel(), args);
                        break;
                }
                FrequencyControlBaseDTO dto = new FrequencyControlBaseDTO();
                dto.setKey(prefixKey + ":" + key);
                dto.setCount(frequency.count());
                dto.setTime(frequency.time());
                dto.setUnit(frequency.unit());
                dtoList.add(dto);
            }
            return FrequencyControlUtils.handle(dtoList, joinPoint::proceed, TOTAL_COUNT_WITH_FIX_TIME_STRATEGY_NAME);
        }
        return joinPoint.proceed();
    }
}
