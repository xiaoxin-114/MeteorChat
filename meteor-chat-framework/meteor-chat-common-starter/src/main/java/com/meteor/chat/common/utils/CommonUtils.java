package com.meteor.chat.common.utils;


import java.lang.reflect.Method;

public class CommonUtils {

    public static String getDefaultPrefix(Method method) {
        return method.getDeclaringClass() + "#" + method.getName();
    }
}
