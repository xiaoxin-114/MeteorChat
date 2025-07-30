package com.meteor.chat.common.core;
@FunctionalInterface
public interface SupplierThrow<T> {

    T get() throws Throwable;
}
