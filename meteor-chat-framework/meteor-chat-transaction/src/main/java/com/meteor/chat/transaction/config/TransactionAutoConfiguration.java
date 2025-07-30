package com.meteor.chat.transaction.config;

import com.meteor.chat.transaction.core.annotation.SecureInvokeConfigurer;
import com.meteor.chat.transaction.core.api.SecureInvokeRecordApi;
import com.meteor.chat.transaction.core.aspect.SecureInvokeAspect;
import com.meteor.chat.transaction.core.service.SecureInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Configuration
@Slf4j
@EnableScheduling
@ConditionalOnProperty(name = "meteor.chat.transaction.enable", havingValue = "true")
public class TransactionAutoConfiguration {
    private Executor executor;

    @Autowired
    void setConfigures(ObjectProvider<SecureInvokeConfigurer> provider) {
        SingletonSupplier<SecureInvokeConfigurer> supplier = SingletonSupplier.of(() -> {
            List<SecureInvokeConfigurer> configurers = provider.stream().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(configurers)) {
                return null;
            }
            if (configurers.size() > 1) {
                throw new IllegalStateException("Only one SecureInvokeConfigurer may exist");
            }
            return configurers.get(0);
        });
        executor = Optional.ofNullable(supplier.get()).map(configure -> configure.getSecureInvokeExecutor()).orElse(ForkJoinPool.commonPool());
    }

    @Bean
    public SecureInvokeService secureInvokeService(SecureInvokeRecordApi secureInvokeApi){
        return new SecureInvokeService(executor, secureInvokeApi);
    }

    @Bean
    public SecureInvokeAspect secureInvokeAspect(SecureInvokeService secureInvokeService){
        return new SecureInvokeAspect(secureInvokeService);
    }

}
