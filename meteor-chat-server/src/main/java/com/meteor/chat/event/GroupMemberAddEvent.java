package com.meteor.chat.event;

import com.meteor.chat.common.domain.entity.GroupMember;
import com.meteor.chat.common.domain.entity.RoomGroup;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
@Getter
public class GroupMemberAddEvent extends ApplicationEvent {
    private final List<GroupMember> memberList;
    private final RoomGroup roomGroup;
    private final Long inviteUid;

    public GroupMemberAddEvent(Object source, List<GroupMember> memberList, RoomGroup roomGroup, Long inviteUid) {
        super(source);
        this.memberList = memberList;
        this.roomGroup = roomGroup;
        this.inviteUid = inviteUid;
    }
}
