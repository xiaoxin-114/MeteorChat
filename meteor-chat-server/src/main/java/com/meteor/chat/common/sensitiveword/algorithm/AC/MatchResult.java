package com.meteor.chat.common.sensitiveword.algorithm.AC;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MatchResult {

    /**
     * 敏感词在内容中的初始下标
     */
    private int startIndex;

    /**
     * 敏感词在内容中的的结尾下标+1
     */
    private int endIndex;

    @Override
    public String toString() {
        return "MatchResult{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }
}
