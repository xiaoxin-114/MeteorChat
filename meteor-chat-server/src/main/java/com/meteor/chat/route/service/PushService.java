package com.meteor.chat.route.service;

import com.meteor.chat.websocket.domain.vo.WSBaseResp;

import java.util.List;

public interface PushService {
    void pushRoomMsg(WSBaseResp<?> msg, List<Long> uidList);
    void pushRoomMsg(WSBaseResp<?> msg);
    void pushSingleMsg(WSBaseResp<?> msg, Long uid);
}
