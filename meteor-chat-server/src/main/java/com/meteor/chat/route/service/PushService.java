package com.meteor.chat.route.service;

import com.meteor.chat.websocket.domain.vo.WSBaseResp;

import java.util.List;

public interface PushService {
    void pushMsg(WSBaseResp<?> msg, List<Long> uidList);
    void pushMsg(WSBaseResp<?> msg);
    void pushMsg(WSBaseResp<?> msg, Long uid);
}
