package com.meteor.chat.event.listener;

import com.meteor.chat.chat.service.cache.GroupMemberCache;
import com.meteor.chat.common.domain.dto.MessageRecallDTO;
import com.meteor.chat.event.MessageRecallEvent;
import com.meteor.chat.route.service.PushService;
import com.meteor.chat.websocket.adapter.WSAdapter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class MessageRecallEventListener{

    @Resource
    private PushService pushService;

    @Resource
    private GroupMemberCache groupMemberCache;
    // 推送撤回消息给群聊的所有成员
    @EventListener(value = MessageRecallEvent.class)
    public void sendMsgToAll(MessageRecallEvent event) {
        MessageRecallDTO dto = event.getMessageRecallDTO();
        List<Long> memberUidList = groupMemberCache.getMemberUidList(dto.getRoomId());
        pushService.pushMsg(WSAdapter.buildMsgRecall(dto), memberUidList);
    }
}
