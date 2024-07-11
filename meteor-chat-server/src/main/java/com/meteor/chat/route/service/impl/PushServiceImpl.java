package com.meteor.chat.route.service.impl;

import com.meteor.chat.route.service.PushService;
import com.meteor.chat.websocket.domain.vo.WSBaseResp;
import com.meteor.chat.websocket.service.WebSocketService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PushServiceImpl implements PushService {

    @Resource
    private WebSocketService webSocketService;

    @Override
    public void pushMsg(WSBaseResp<?> msg, List<Long> uidList) {
        uidList.forEach(id -> pushMsg(msg, id));
    }

    @Override
    public void pushMsg(WSBaseResp<?> msg) {
        webSocketService.sendToAllOnline(msg);
    }

    @Override
    public void pushMsg(WSBaseResp<?> msg, Long uid) {
        // 过滤掉channel不在本服务器上的用户
        if (!webSocketService.haveUid(uid)) {
            return;
        }
        webSocketService.sendToUid(msg, uid);
    }
}
