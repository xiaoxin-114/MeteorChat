package com.meteor.chat.transaction.annotation;


import java.util.concurrent.Executor;

public interface SecureInvokeConfigurer {

    Executor getSecureInvokeExecutor();
}
