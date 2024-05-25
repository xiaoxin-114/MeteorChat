package com.meteor.chat.msg.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.entity.RoomFriend;
import com.meteor.chat.common.domain.enums.HotFlagEunm;
import com.meteor.chat.common.domain.enums.RoomFriendStatusEnum;
import com.meteor.chat.common.mapper.RoomFriendMapper;
import com.meteor.chat.common.mapper.RoomMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomFriendDao extends ServiceImpl<RoomFriendMapper, RoomFriend> {

    public RoomFriend findByRoomKeyList(String roomKey) {
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
