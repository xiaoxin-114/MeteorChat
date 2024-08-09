package com.meteor.chat.common.sensitiveword;

import com.meteor.chat.common.sensitiveword.algorithm.DFA.DFAFilter;

import java.util.List;
import java.util.Objects;

public class SensitiveWords {

    private SensitiveWordFilter filter = DFAFilter.getInstance();
    private IWordFactory wordFactory;
    private static SensitiveWords instance;

    public static SensitiveWords getInstance() {
        synchronized (SensitiveWords.class) {
            if (Objects.isNull(instance)) {
                instance = new SensitiveWords();
            }
        }
        return instance;
    }

    public void init() {
        List<String> wordList = wordFactory.getWordList();
        filter.loadWord(wordList);
    }


    /**
     * 过滤策略
     *
     * @param filter 过滤器
     * @return 结果
     * @since 0.7.0
     */
    public void filterStrategy(SensitiveWordFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("filter can not be null");
        }
        this.filter = filter;
    }

    public void sensitiveWord(IWordFactory wordFactory) {
        if (wordFactory == null) {
            throw new IllegalArgumentException("wordFactory can not be null");
        }
        this.wordFactory = wordFactory;
    }

    public boolean hasSensitiveWord(String content) {
        return this.filter.hasSensitiveWord(content);
    }

    public String filter(String content) {
        return this.filter.filter(content);
    }
}
