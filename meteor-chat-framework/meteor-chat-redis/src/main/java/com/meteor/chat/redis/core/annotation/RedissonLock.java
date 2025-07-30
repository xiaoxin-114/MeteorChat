package com.meteor.chat.redis.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RedissonLock {

    /**
     * 锁的key的前缀值，默认使用方法的全限定名
     * @return
     */
    String prefixKey() default "";

    /**
     * 锁等待时间，-1表示直接失败
     * @return
     */
    long time() default -1;

    /**
     * 锁等待时间单位，默认毫秒
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * spel表达式来作为锁的key值
     * @return
     */
    String key() default "";
}
