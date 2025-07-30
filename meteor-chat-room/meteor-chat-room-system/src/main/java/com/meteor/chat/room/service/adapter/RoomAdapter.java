package com.meteor.chat.room.service.adapter;

import com.meteor.chat.api.room.enums.HotFlagEunm;
import com.meteor.chat.api.room.enums.RoomTypeEnum;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.room.domain.dto.ChatRoomDTO;
import com.meteor.chat.room.domain.entity.GroupMember;
import com.meteor.chat.room.domain.entity.Room;
import com.meteor.chat.room.domain.entity.RoomFriend;
import com.meteor.chat.room.domain.entity.RoomGroup;
import com.meteor.chat.room.enums.GroupRoleAPPEnum;
import com.meteor.chat.room.enums.RoomFriendStatusEnum;
import com.meteor.chat.room.domain.vo.ChatRoomResp;
import com.meteor.chat.room.domain.vo.GroupMemberListResp;
import com.meteor.chat.room.domain.vo.GroupMemberResp;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomAdapter {

    public static List<ChatRoomResp> buildResp(List<Room> list) {
        return list.stream().map(RoomAdapter::buildResp).collect(Collectors.toList());
    }

    public static ChatRoomResp buildResp(Room room) {
        ChatRoomResp chatRoomResp = new ChatRoomResp();
        chatRoomResp.setRoomId(room.getId());
        chatRoomResp.setType(room.getType());
        chatRoomResp.setHot_Flag(room.getHotFlag());
        chatRoomResp.setActiveTime(room.getActiveTime());
        return chatRoomResp;
    }

    public static ChatRoomResp buildResp(ChatRoomDTO room) {
        ChatRoomResp chatRoomResp = new ChatRoomResp();
        chatRoomResp.setRoomId(room.getRoomId());
        chatRoomResp.setType(room.getType());
        chatRoomResp.setHot_Flag(room.getHot_Flag());
        chatRoomResp.setActiveTime(room.getActiveTime());
        chatRoomResp.setName(room.getName());
        chatRoomResp.setAvatar(room.getAvatar());
        return chatRoomResp;
    }

    public static ChatRoomDTO buildDTO(Room room) {
        ChatRoomDTO chatRoom = new ChatRoomDTO();
        chatRoom.setRoomId(room.getId());
        chatRoom.setType(room.getType());
        chatRoom.setHot_Flag(room.getHotFlag());
        chatRoom.setActiveTime(room.getActiveTime());
        chatRoom.setLastMsgId(room.getLastMsgId());
        return chatRoom;
    }

    public static RoomFriend buildFriendRoom(Long uid1, Long uid2, Long roomId) {
        RoomFriend roomFriend = new RoomFriend();
        if (uid1 > uid2) {
            roomFriend.setUid1(uid2);
            roomFriend.setUid2(uid1);
        } else {
            roomFriend.setUid2(uid2);
            roomFriend.setUid1(uid1);
        }
        roomFriend.setRoomKey(roomFriend.getUid1() + "_" + roomFriend.getUid2());
        roomFriend.setStatus(RoomFriendStatusEnum.NORAML.getCode());
        roomFriend.setRoomId(roomId);
        return roomFriend;
    }

    public static Room buildRoom(RoomTypeEnum roomTypeEnum) {
        Room room = new Room();
        room.setType(roomTypeEnum.getCode());
        room.setActiveTime(new Date());
        room.setHotFlag(HotFlagEunm.NORAML.getCode());
        // 定义0为聊天室最新消息的id，为了保证更新聊天室最新活跃时间和最新消息id能够成功
        // 如果为空，有个比较消息id和最新发送消息id的条件无法满足
        // 也可以通过数据库表定义语句default来定义，contact表格也同理
//        room.setLastMsgId(0L);
        return room;
    }

    public static String buildRoomKey(Long uid1, Long uid2) {
        return uid1 < uid2 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }

    /**
     * 根据单聊和用户id获取好友id
     * @param roomFriend
     * @param uid
     * @return
     */
    public static Long getFriendUid(RoomFriend roomFriend, Long uid) {
        return uid.equals(roomFriend.getUid1()) ? roomFriend.getUid2() : roomFriend.getUid1();
    }

    public static List<GroupMemberResp> buildMemberResp(List<UserInfoDTO> userList, List<GroupMember> memberList) {
        Map<Long, Integer> roleMap = memberList.stream().collect(Collectors.toMap(GroupMember::getUid, GroupMember::getRole));
        return userList.stream().map(user -> {
            Integer role = roleMap.get(user.getUid());
            return GroupMemberResp.builder()
                    .uid(user.getUid())
                    .activeStatus(user.getActiveStatus())
                    .lastOptTime(user.getLastOptTime())
                    .roleId(role)
                    .build();
        }).collect(Collectors.toList());
    }

    public static List<GroupMemberListResp> buildMemberListResp(List<UserInfoDTO> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return Collections.emptyList();
        }
        return userList.stream().map(user -> {
            GroupMemberListResp listResp = new GroupMemberListResp();
            listResp.setUid(user.getUid());
            listResp.setName(user.getName());
            listResp.setAvatar(user.getAvatar());
            return listResp;
        }).collect(Collectors.toList());
    }

    public static RoomGroup buildRoomGroup(UserInfoDTO user, Room room) {
        RoomGroup roomGroup = new RoomGroup();
        roomGroup.setRoomId(room.getId());
        roomGroup.setName(user.getName() + "的群聊");
        roomGroup.setAvatar(user.getAvatar());
        return roomGroup;
    }

    /**
     * 创建一个的groupMember
     * @param uid
     * @return
     */
    public static GroupMember buildGroupMember(Long uid, RoomGroup roomGroup, GroupRoleAPPEnum role) {
        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(roomGroup.getId());
        groupMember.setUid(uid);
        groupMember.setRole(role.getCode());
        return groupMember;
    }
}
