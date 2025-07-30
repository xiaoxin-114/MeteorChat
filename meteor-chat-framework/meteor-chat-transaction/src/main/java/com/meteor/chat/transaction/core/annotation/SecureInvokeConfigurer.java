package com.meteor.chat.transaction.core.annotation;


import java.util.concurrent.Executor;

public interface SecureInvokeConfigurer {

    Executor getSecureInvokeExecutor();
}
