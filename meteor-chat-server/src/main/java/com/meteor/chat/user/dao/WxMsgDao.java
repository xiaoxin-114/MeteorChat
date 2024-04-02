package com.meteor.chat.user.dao;

import com.meteor.chat.common.domain.entity.WxMsg;

public interface WxMsgDao {
    void save(WxMsg msg);
}
