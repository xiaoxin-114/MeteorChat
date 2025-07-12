package com.meteor.chat.event.listener;

import com.meteor.chat.chat.service.cache.GroupMemberCache;
import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.domain.entity.GroupMember;
import com.meteor.chat.common.domain.entity.RoomGroup;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.vo.req.ChatMessageReq;
import com.meteor.chat.event.GroupMemberAddEvent;
import com.meteor.chat.msg.service.MessageService;
import com.meteor.chat.msg.service.adapter.MsgAdapter;
import com.meteor.chat.route.service.PushService;
import com.meteor.chat.user.service.cache.UserCache;
import com.meteor.chat.user.service.cache.UserInfoCache;
import com.meteor.chat.websocket.adapter.WSAdapter;
import com.meteor.chat.websocket.domain.vo.WSBaseResp;
import com.meteor.chat.websocket.domain.vo.WSMemberChange;
import org.springframework.util.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GroupMemberAddEventListener {

    @Resource
    private MessageService messageService;

    @Resource
    private PushService pushService;

    @Resource
    private UserInfoCache userInfoCache;

    @Resource
    private GroupMemberCache groupMemberCache;

    @EventListener(value = GroupMemberAddEvent.class)
    public void pushNewMemberMsg(GroupMemberAddEvent event) {
        // 向群组所有成员推送新成员的消息
        Long uid = event.getInviteUid();
        RoomGroup roomGroup = event.getRoomGroup();
        List<GroupMember> memberList = event.getMemberList();
        if (CollectionUtils.isEmpty(memberList)) {
            return;
        }
        Map<Long, User> userMap = userInfoCache.getBatch(memberList.stream().map(GroupMember::getUid).collect(Collectors.toList()));
        User inviteUser = userInfoCache.get(uid);
        // 拼接系统消息的内容
        StringBuilder content = new StringBuilder();
        content.append(String.format("\"%s\"", inviteUser.getName()));
        content.append(" 邀请 ");
        for(User user
                : userMap.values()) {
            content.append(String.format("\"%s\" ", user.getName()));
            content.append(",");
        }
        content = content.deleteCharAt(content.length() - 1);
        content.append("加入了群聊");
        ChatMessageReq msgReq = MsgAdapter.buildMemberChange(roomGroup.getRoomId(), content.toString());
        messageService.sendMsg(msgReq, CommonConstants.SYSTEM_UID);
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
        Map<Long, User> userMap = userInfoCache.getBatch(uidList);
        userMap.values().forEach(user -> {
            WSBaseResp<WSMemberChange> memberAddWsResp = WSAdapter.buildGroupMemberAdd(roomGroup.getRoomId(), user);
            pushService.pushMsg(memberAddWsResp, memberUidList);
        });
        // 清除缓存
        groupMemberCache.evictMemberUidList(roomGroup.getRoomId());
    }
}
