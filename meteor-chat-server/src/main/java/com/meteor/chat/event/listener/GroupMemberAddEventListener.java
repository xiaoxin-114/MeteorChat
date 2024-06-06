package com.meteor.chat.event.listener;

import com.meteor.chat.event.GroupMemberAddEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class GroupMemberAddEventListener {

    @EventListener(value = GroupMemberAddEvent.class)
    public void pushNewMemberMsg(GroupMemberAddEvent event) {
        // 向群组所有成员推送新成员的消息
    }

    @EventListener(value = GroupMemberAddEvent.class)
    public void pushGroupChange(GroupMemberAddEvent event) {
        // 向在线用户推送用户成员发送变化的消息
    }
}
