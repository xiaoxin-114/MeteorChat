package com.meteor.chat.common.config;

import com.meteor.chat.common.sensitiveword.MyWordFactory;
import com.meteor.chat.common.sensitiveword.SensitiveWords;
import com.meteor.chat.common.sensitiveword.algorithm.AC.ACTrieFilter;
import com.meteor.chat.common.sensitiveword.algorithm.DFA.DFAFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class SensitiveConfig {

    @Resource
    private MyWordFactory myWordFactory;

    @Bean
    public SensitiveWords sensitiveWords() {
        SensitiveWords instance = SensitiveWords.getInstance();
        instance.sensitiveWord(myWordFactory);
        instance.filterStrategy(ACTrieFilter.getInstance());
        instance.init();
        return instance;
    }
}
