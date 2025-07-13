package com.meteor.chat.event.listener;

import com.meteor.chat.chat.service.cache.GroupMemberCache;
import com.meteor.chat.chat.service.cache.RoomCache;
import com.meteor.chat.common.domain.dto.MessageRecallDTO;
import com.meteor.chat.common.domain.entity.Room;
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
    private RoomCache roomCache;

    @Resource
    private GroupMemberCache groupMemberCache;
    // 推送撤回消息给群聊的所有成员
    @EventListener(value = MessageRecallEvent.class)
    public void sendMsgToAll(MessageRecallEvent event) {
        MessageRecallDTO dto = event.getMessageRecallDTO();
        Room room = roomCache.get(dto.getRoomId());
        // 全员群
        if (room.isHotRoom()) {
            pushService.pushRoomMsg(WSAdapter.buildMsgRecall(dto));
        } else {
            List<Long> memberUidList = groupMemberCache.getMemberUidList(dto.getRoomId());
            pushService.pushRoomMsg(WSAdapter.buildMsgRecall(dto), memberUidList);
        }
    }
}
