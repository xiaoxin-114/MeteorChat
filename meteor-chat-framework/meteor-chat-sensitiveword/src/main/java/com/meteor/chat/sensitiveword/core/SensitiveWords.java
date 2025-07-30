package com.meteor.chat.sensitiveword.core;


import com.meteor.chat.sensitiveword.core.algorithm.DFA.DFAFilter;

import java.util.List;
import java.util.Objects;

public class SensitiveWords {

    private SensitiveWordFilter filter = DFAFilter.getInstance();
    private IWordFactory wordFactory;
    private volatile boolean init = false;


    /**
     * 只在第一次使用时初始化，创建对象时不初始化
     * 避免初始化失败的问题
     */
    private void checkInit() {
        if (init) {
            return;
        }
        synchronized (this) {
            if (!init) {
                List<String> wordList = wordFactory.getWordList();
                filter.loadWord(wordList);
                init = true;
            }
        }
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
        checkInit();
        return this.filter.hasSensitiveWord(content);
    }

    public String filter(String content) {
        checkInit();
        return this.filter.filter(content);
    }
}
