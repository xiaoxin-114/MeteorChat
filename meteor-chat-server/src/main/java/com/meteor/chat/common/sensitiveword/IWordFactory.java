package com.meteor.chat.common.sensitiveword;

import java.util.List;

public interface IWordFactory {

    /**
     * 加载敏感词列表
     * @return
     */
    List<String> getWordList();
}
