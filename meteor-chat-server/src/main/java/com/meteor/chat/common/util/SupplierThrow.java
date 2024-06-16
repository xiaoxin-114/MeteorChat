package com.meteor.chat.common.util;
@FunctionalInterface
public interface SupplierThrow<T> {

    T get() throws Throwable;
}
