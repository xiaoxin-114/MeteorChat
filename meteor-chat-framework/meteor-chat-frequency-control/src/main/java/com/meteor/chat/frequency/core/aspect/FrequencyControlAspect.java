package com.meteor.chat.frequency.core.aspect;

import com.meteor.chat.common.utils.CommonUtils;
import com.meteor.chat.common.utils.SpElUtils;
import com.meteor.chat.frequency.core.annotation.FrequencyControl;
import com.meteor.chat.frequency.core.dto.FrequencyControlBaseDTO;
import com.meteor.chat.frequency.core.utils.FrequencyControlUtils;
import com.meteor.chat.web.core.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Slf4j
public class FrequencyControlAspect {
    private static final String TOTAL_COUNT_WITH_FIX_TIME_STRATEGY_NAME = "TotalCountWithInFixTime";

    @Pointcut(value = "@annotation(com.meteor.chat.frequency.core.annotation.FrequencyControl) || @annotation(com.meteor.chat.frequency.core.annotation.FrequencyControlContainer)")
    private void pointcut(){}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
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
