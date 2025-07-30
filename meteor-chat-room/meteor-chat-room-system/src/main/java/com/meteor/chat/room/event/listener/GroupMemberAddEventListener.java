package com.meteor.chat.room.event.listener;

import com.meteor.chat.api.msg.MessageCommonApi;
import com.meteor.chat.api.msg.dto.MemberAddMsgDTO;
import com.meteor.chat.api.user.UserInfoCommonApi;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.push.common.core.push.PushService;
import com.meteor.chat.push.common.domain.vo.WSBaseResp;
import com.meteor.chat.room.adapter.WSAdapter;
import com.meteor.chat.room.domain.entity.GroupMember;
import com.meteor.chat.room.domain.entity.RoomGroup;
import com.meteor.chat.room.domain.vo.WSMemberChange;
import com.meteor.chat.room.event.GroupMemberAddEvent;
import com.meteor.chat.room.service.cache.GroupMemberCache;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GroupMemberAddEventListener {

    @Resource
    private MessageCommonApi messageCommonApi;

    @Resource
    private PushService pushService;

    @Resource
    private GroupMemberCache groupMemberCache;

    @Resource
    private UserInfoCommonApi userInfoCommonApi;

    @EventListener(value = GroupMemberAddEvent.class)
    public void pushNewMemberMsg(GroupMemberAddEvent event) {
        // 向群组所有成员推送新成员的消息
        Long uid = event.getInviteUid();
        RoomGroup roomGroup = event.getRoomGroup();
        List<GroupMember> memberList = event.getMemberList();
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }
        List<Long> memberUidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        messageCommonApi.sendMemberAddMsg(MemberAddMsgDTO.builder()
                .inviter(uid)
                .roomId(roomGroup.getRoomId())
                .memberUidList(memberUidList).build());
    }

    @EventListener(value = GroupMemberAddEvent.class)
    public void pushGroupChange(GroupMemberAddEvent event) {
        RoomGroup roomGroup = event.getRoomGroup();
        // 向在线用户推送用户成员发送变化的消息，全成员列表发送变化
        List<GroupMember> memberList = event.getMemberList();
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        List<UserInfoDTO> userList = userInfoCommonApi.getUserInfoList(uidList);
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }
        userList.stream().filter(Objects::isNull).forEach(user -> {
            WSBaseResp<WSMemberChange> memberAddWsResp = WSAdapter.buildGroupMemberAdd(roomGroup.getRoomId(), user);
            pushService.pushRoomMsg(memberAddWsResp, memberUidList);
        });
        // 清除缓存
        groupMemberCache.evictMemberUidList(roomGroup.getRoomId());
    }
}
