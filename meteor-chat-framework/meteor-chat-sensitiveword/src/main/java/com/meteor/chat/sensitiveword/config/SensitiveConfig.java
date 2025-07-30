package com.meteor.chat.sensitiveword.config;

import com.meteor.chat.sensitiveword.core.IWordFactory;
import com.meteor.chat.sensitiveword.core.MyWordFactory;
import com.meteor.chat.sensitiveword.core.SensitiveWords;
import com.meteor.chat.sensitiveword.core.algorithm.AC.ACTrieFilter;
import com.meteor.chat.sensitiveword.core.api.SensitiveWordApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SensitiveConfig {

    @Bean
    public IWordFactory myWordFactory(SensitiveWordApi sensitiveWordApi) {
        return new MyWordFactory(sensitiveWordApi);
    }

    @Bean
    public SensitiveWords sensitiveWords(IWordFactory wordFactory) {
        SensitiveWords sensitiveWords = new SensitiveWords();
        sensitiveWords.sensitiveWord(wordFactory);
        sensitiveWords.filterStrategy(ACTrieFilter.getInstance());
        return sensitiveWords;
    }
}
