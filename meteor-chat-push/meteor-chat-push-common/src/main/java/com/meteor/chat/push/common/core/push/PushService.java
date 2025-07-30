package com.meteor.chat.push.common.core.push;


import com.meteor.chat.push.common.domain.vo.WSBaseResp;

import java.util.List;

public interface PushService {
    void pushRoomMsg(WSBaseResp<?> msg, List<Long> uidList);
    void pushRoomMsg(WSBaseResp<?> msg);
    void pushSingleMsg(WSBaseResp<?> msg, Long uid);
}
