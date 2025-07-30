package com.meteor.chat.web.core.context;


import com.meteor.chat.web.core.domian.RequestInfo;

import java.util.Optional;

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

    public static Long getUid() {
        return Optional.ofNullable(get()).map(RequestInfo::getUid).orElse(null);
    }

    public static String getIp() {
        return Optional.ofNullable(get()).map(RequestInfo::getIp).orElse(null);
    }
}
