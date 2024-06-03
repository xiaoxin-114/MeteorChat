package com.meteor.chat.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.RoomFriend;
import com.meteor.chat.common.domain.enums.RoomFriendStatusEnum;
import com.meteor.chat.common.mapper.RoomFriendMapper;
import org.springframework.stereotype.Repository;

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
}
