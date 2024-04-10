package com.meteor.chat.common.util;

import com.meteor.chat.common.domain.RequestInfo;

public class UserContext {

    private static final ThreadLocal<RequestInfo> context = new ThreadLocal<>();

    public static void set(RequestInfo requestInfo) {
        context.set(requestInfo);
    }

    public static RequestInfo get() {
        return context.get();
    }

    public static void remove() {
        context.remove();
    }
}
