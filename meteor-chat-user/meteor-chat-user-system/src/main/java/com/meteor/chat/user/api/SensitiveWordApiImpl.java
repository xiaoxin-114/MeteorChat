package com.meteor.chat.user.api;

import com.meteor.chat.sensitiveword.core.api.SensitiveWordApi;
import com.meteor.chat.sensitiveword.core.domain.SensitiveWord;
import com.meteor.chat.user.dao.SensitiveWordDao;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
@RestController
public class SensitiveWordApiImpl implements SensitiveWordApi {

    @Resource
    private SensitiveWordDao sensitiveWordDao;
    @Override
    public List<SensitiveWord> listSensitiveWord() {
        return sensitiveWordDao.list();
    }
}
