package com.meteor.chat.frequency.core.annotation;


import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Repeatable(FrequencyControlContainer.class)
public @interface FrequencyControl {

    /**
     * key的前缀,默认取方法全限定名，除非我们在不同方法上对同一个资源做频控，就自己指定
     */
    String prefixKey() default "";

    /**
     * SPEL表达式，当type喂SPEL时必填
     */
    String spel() default "";

    /**
     * 限制类型
     *
     * @return
     */
    FrequencyTypeEnum type();

    /**
     * 限制时间
     *
     * @return
     */
    int time();

    /**
     * 时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 限制次数
     *
     * @return
     */
    int count();

    enum FrequencyTypeEnum {
        IP,UID,SPEL;
    }
}
