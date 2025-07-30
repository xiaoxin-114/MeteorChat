package com.meteor.chat.sensitiveword.core;

import com.meteor.chat.sensitiveword.core.domain.SensitiveWord;
import com.meteor.chat.sensitiveword.core.api.SensitiveWordApi;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;
@RequiredArgsConstructor
public class MyWordFactory implements IWordFactory {

    private final SensitiveWordApi sensitiveWordApi;

    @Override
    public List<String> getWordList() {
        List<SensitiveWord> list = sensitiveWordApi.listSensitiveWord();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().map(SensitiveWord::getWord).collect(Collectors.toList());
    }
}
