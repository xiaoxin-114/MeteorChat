package com.meteor.chat.room.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.room.domain.entity.RoomFriend;
import com.meteor.chat.room.enums.RoomFriendStatusEnum;
import com.meteor.chat.room.mapper.RoomFriendMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomFriendDao extends ServiceImpl<RoomFriendMapper, RoomFriend> {

    public RoomFriend findByRoomKey(String roomKey) {
        return lambdaQuery().eq(RoomFriend::getRoomKey, roomKey)
                .one();
    }

    public void updateRoomStatus(Long id, RoomFriendStatusEnum status) {
        lambdaUpdate().eq(RoomFriend::getId, id)
                .set(RoomFriend::getStatus, status.getCode())
                .update();
    }

    public void disableRoomByKey(String buildRoomKey) {
        lambdaUpdate().eq(RoomFriend::getRoomKey, buildRoomKey)
                .set(RoomFriend::getStatus, RoomFriendStatusEnum.FORBID.getCode())
                .update();
    }

    public List<RoomFriend> listByRoomIds(List<Long> roomIds) {
        return lambdaQuery().in(RoomFriend::getRoomId, roomIds)
                .list();
    }
}
