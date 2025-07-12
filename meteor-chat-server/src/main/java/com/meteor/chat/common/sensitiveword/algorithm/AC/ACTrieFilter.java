package com.meteor.chat.common.sensitiveword.algorithm.AC;

import com.meteor.chat.common.sensitiveword.SensitiveWordFilter;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class ACTrieFilter implements SensitiveWordFilter {

    private ACTrie acTrie;
    private static ACTrieFilter instance;
    private static final char REPLACE = '*';

    public synchronized static ACTrieFilter getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ACTrieFilter();
        }
        return instance;
    }

    @Override
    public boolean hasSensitiveWord(String text) {
        return !CollectionUtils.isEmpty(acTrie.filter(text));
    }

    @Override
    public String filter(String text) {
        StringBuilder builder = new StringBuilder(text);
        List<MatchResult> matchResults = acTrie.filter(text);
        for(MatchResult result : matchResults) {
            for (int i = result.getStartIndex(); i < result.getEndIndex(); i++) {
                builder.setCharAt(i, REPLACE);
            }
        }
        return builder.toString();
    }

    @Override
    public void loadWord(List<String> words) {
        if (CollectionUtils.isEmpty(words)) {
            throw new IllegalArgumentException("敏感词列表为空，过滤器构建失败");
        }
        acTrie = new ACTrie(words);
    }
}
