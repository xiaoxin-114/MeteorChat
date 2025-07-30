package com.meteor.chat.frequency.config;

import com.meteor.chat.frequency.core.aspect.FrequencyControlAspect;
import com.meteor.chat.frequency.core.strategy.TotalCountFrequencyControlStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FrequencyAutoConfiguration {

    @Bean
    public FrequencyControlAspect frequencyControlAspect() {
        return new FrequencyControlAspect();
    }

    @Bean
    public TotalCountFrequencyControlStrategy totalCountFrequencyControlStrategy() {
        return new TotalCountFrequencyControlStrategy();
    }
}
