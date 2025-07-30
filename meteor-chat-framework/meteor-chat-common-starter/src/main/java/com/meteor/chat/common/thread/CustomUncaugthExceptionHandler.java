package com.meteor.chat.common.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * 处理线程中未捕获的异常，保证能把异常写在日志文件中
 */
@Slf4j
public class CustomUncaugthExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final CustomUncaugthExceptionHandler instance = new CustomUncaugthExceptionHandler();

    public static CustomUncaugthExceptionHandler getInstance(){
        return instance;
    }

    private CustomUncaugthExceptionHandler(){}

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread {} ", t.getName(), e);
    }
}
