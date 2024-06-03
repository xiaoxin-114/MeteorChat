package com.meteor.chat.chat.service.adapter;
import java.util.Date;

import com.meteor.chat.common.domain.dto.ChatRoomDTO;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.entity.RoomFriend;
import com.meteor.chat.common.domain.enums.HotFlagEunm;
import com.meteor.chat.common.domain.enums.RoomFriendStatusEnum;
import com.meteor.chat.common.domain.enums.RoomTypeEnum;
import com.meteor.chat.common.domain.vo.ChatRoomResp;
import org.springframework.beans.BeanUtils;

import java.util.List;
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
        room.setHotFlag(HotFlagEunm.NORAML.getCode());
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
}
