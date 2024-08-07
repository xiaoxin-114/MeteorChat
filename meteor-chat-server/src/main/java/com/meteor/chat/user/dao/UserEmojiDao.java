package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.UserEmoji;
import com.meteor.chat.common.mapper.UserEmojiMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserEmojiDao extends ServiceImpl<UserEmojiMapper, UserEmoji> {
    public List<UserEmoji> listByUid(Long uid) {
        return lambdaQuery().eq(UserEmoji::getUid, uid).list();
    }

    public int countByUid(Long uid) {
        return lambdaQuery().eq(UserEmoji::getUid, uid).count();
    }

    public List<UserEmoji> getByUidAndUrl(Long uid, String expressionUrl) {
        return lambdaQuery().eq(UserEmoji::getUid, uid).eq(UserEmoji::getExpressionUrl, expressionUrl).list();
    }
}
