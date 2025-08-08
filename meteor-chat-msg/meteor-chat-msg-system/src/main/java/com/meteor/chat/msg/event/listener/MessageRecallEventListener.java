package com.meteor.chat.msg.event.listener;

import com.meteor.chat.api.room.RoomCommonApi;
import com.meteor.chat.api.room.RoomMemberCommonApi;
import com.meteor.chat.api.room.dto.RoomInfoDTO;
import com.meteor.chat.msg.adapter.WSAdapter;
import com.meteor.chat.msg.domain.dto.MessageRecallDTO;
import com.meteor.chat.msg.event.MessageRecallEvent;
import com.meteor.chat.push.common.core.push.PushService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class MessageRecallEventListener{

    @Resource
    private PushService pushService;

    @DubboReference
    private RoomCommonApi roomCommonApi;

    @DubboReference
    private RoomMemberCommonApi roomMemberCommonApi;
    // 推送撤回消息给群聊的所有成员
    @EventListener(value = MessageRecallEvent.class)
    public void sendMsgToAll(MessageRecallEvent event) {
        MessageRecallDTO dto = event.getMessageRecallDTO();
        RoomInfoDTO room = roomCommonApi.getRoomInfo(dto.getRoomId()).getCheckData();
        // 全员群
        if (room.isHotRoom()) {
            pushService.pushRoomMsg(WSAdapter.buildMsgRecall(dto));
        } else {
            List<Long> memberUidList = roomMemberCommonApi.getMemberList(dto.getRoomId()).getCheckData();
            pushService.pushRoomMsg(WSAdapter.buildMsgRecall(dto), memberUidList);
        }
    }
}
