package com.meteor.chat.transaction.core.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.meteor.chat.transaction.core.api.SecureInvokeRecordApi;
import com.meteor.chat.transaction.core.domian.SecureInvokeDTO;
import com.meteor.chat.transaction.core.domian.SecureInvokeRecord;
import com.meteor.chat.common.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class SecureInvokeService {
    private static final double RETRY_INTERVAL_MINUTES = 2D;
    private Executor executor;
    private SecureInvokeRecordApi secureInvokeRecordApi;

//    @Scheduled(cron = "* */5 * * * ?")
    private void scheduleInvoke() {
        List<SecureInvokeRecord> records = secureInvokeRecordApi.getRecords();
        for (SecureInvokeRecord record : records) {
            doInvoke(record);
        }
    }

    public void invoke(SecureInvokeRecord invokeRecord, Boolean async) {
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        // 如果不在事务中则直接处理
        if (!inTransaction) {
            return;
        }
        secureInvokeRecordApi.save(invokeRecord);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            @SneakyThrows
            public void afterCommit() {
                TransactionSynchronization.super.afterCommit();
                invoke0(invokeRecord, async);
            }
        });
    }

    private void retryRecord(SecureInvokeRecord record, String errorMsg) {
        Integer retryTimes = record.getRetryTimes() + 1;
        SecureInvokeRecord update = SecureInvokeRecord.builder()
                .id(record.getId())
                .failReason(errorMsg)
                .nextRetryTime(getNextRetryTime(retryTimes))
                .build();
        if (retryTimes > record.getMaxRetryTimes()) {
            record.setStatus(SecureInvokeRecord.STATUS_FAIL);
        } else {
            update.setRetryTimes(retryTimes);
        }
        secureInvokeRecordApi.updateById(update);
    }

    private Date getNextRetryTime(Integer retryTimes) {//或者可以采用退避算法
        double waitMinutes = Math.pow(RETRY_INTERVAL_MINUTES, retryTimes);//重试时间指数上升 2m 4m 8m 16m
        return DateUtil.offsetMinute(new Date(), (int) waitMinutes);
    }

    private void invoke0(SecureInvokeRecord invokeRecord, boolean async) {
        if (async) {
            doAsyncInvoke(invokeRecord);
        }else {
            doInvoke(invokeRecord);
        }
    }

    private void doAsyncInvoke(SecureInvokeRecord invokeRecord) {
        executor.execute(() -> {
            log.debug(Thread.currentThread().getName());
            doInvoke(invokeRecord);
        });
    }

    private void doInvoke(SecureInvokeRecord invokeRecord) {
        SecureInvokeDTO invokeDTO = invokeRecord.getSecureInvokeDTO();
        try {
            String className = invokeDTO.getClassName();
            Class<?> aClass = Class.forName(className);
            List<String> paramClassString = JsonUtils.toList(invokeDTO.getParameterTypes(), String.class);
            List<Class<?>> parameters = toParameters(paramClassString);
            Object target = SpringUtil.getBean(aClass);
            Method method = ReflectUtil.getMethod(aClass, invokeDTO.getMethodName(), parameters.toArray(new Class[]{}));
            Object[] args = getArgs(invokeDTO.getArgs(), parameters);
            method.invoke(target, args);
            removeDTOById(invokeRecord.getId());
        }catch (Exception e) {
            log.error("SecureInvokeService invoke fail", e);
            retryRecord(invokeRecord, e.getMessage());
        }
    }

    private void removeDTOById(Long id) {
        secureInvokeRecordApi.removeById(id);
    }

    private Object[] getArgs(String args,  List<Class<?>> parameterClasses) {
        JsonNode jsonNode = JsonUtils.toJsonNode(args);
        Object[] result = new Object[jsonNode.size()];
        for (int i = 0; i < jsonNode.size(); i++) {
            Class<?> aClass = parameterClasses.get(i);
            result[i] = JsonUtils.nodeToValue(jsonNode.get(i), aClass);
        }
        return result;
    }

    private List<Class<?>> toParameters(List<String> nameList) {
        return nameList.stream().map(name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                log.error("SecureInvokeService class not fund", e);
            }
            return null;
        }).collect(Collectors.toList());
    }
}
