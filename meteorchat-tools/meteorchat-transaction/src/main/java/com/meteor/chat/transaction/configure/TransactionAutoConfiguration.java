package com.meteor.chat.transaction.configure;

import com.meteor.chat.transaction.annotation.SecureInvokeConfigurer;
import com.meteor.chat.transaction.dao.SecureInvokeDao;
//import com.meteor.chat.transaction.service.MQProducer;
import com.meteor.chat.transaction.service.SecureInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
@ComponentScan("com.meteor.chat.transaction")
@EnableScheduling
@MapperScan(basePackages = "com.meteor.chat.transaction.mapper")
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
    public SecureInvokeService secureInvokeService(SecureInvokeDao secureInvokeDao){
        return new SecureInvokeService(executor, secureInvokeDao);
    }

}
