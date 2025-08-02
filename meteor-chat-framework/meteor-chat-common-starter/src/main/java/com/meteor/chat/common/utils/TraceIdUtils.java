package com.meteor.chat.common.utils;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 生成全局唯一的traceId工具类
 */
public class TraceIdUtils {

    /**
     * 生成高性能 traceId
     * 格式：时间戳(13位) + 进程ID(3位) + 线程ID(5位) + 随机数(4位)
     */
    public static String generateTraceId() {
        StringBuilder sb = new StringBuilder(28);

        // 时间戳 13位
        sb.append(System.currentTimeMillis());

        // 进程ID 3位
        long processId = getProcessId();
        sb.append(String.format("%03d", processId % 1000));

        // 线程ID 5位
        sb.append(String.format("%05d", Thread.currentThread().getId() % 100000));

        // 随机数 4位
        sb.append(String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));

        return sb.toString();
    }

    private static long getProcessId() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(name.split("@")[0]);
    }
}
