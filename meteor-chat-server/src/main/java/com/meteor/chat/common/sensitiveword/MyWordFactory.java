package com.meteor.chat.common.sensitiveword;

import com.meteor.chat.common.domain.entity.SensitiveWord;
import com.meteor.chat.common.sensitiveword.dao.SensitiveWordDao;
import org.springframework.util.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class MyWordFactory implements IWordFactory{

    @Resource
    private SensitiveWordDao sensitiveWordDao;

    @Override
    public List<String> getWordList() {
        List<SensitiveWord> list = sensitiveWordDao.list(null);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().map(SensitiveWord::getWord).collect(Collectors.toList());
    }
}
