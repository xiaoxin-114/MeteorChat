package com.meteor.chat.redis.core.util;

import com.meteor.chat.common.core.SupplierThrow;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.common.exception.CommonErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LockUtil {
    @Resource
    private RedissonClient redissonClient;

    public <T> T executeWithLockWithThrows(String key, Long time, TimeUnit timeUnit, SupplierThrow<T> supplier) throws Throwable {
        RLock lock = redissonClient.getLock(key);
        boolean lockSuccess = lock.tryLock(time, timeUnit);
        if (!lockSuccess) {
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try {
            return supplier.get();
        }finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
