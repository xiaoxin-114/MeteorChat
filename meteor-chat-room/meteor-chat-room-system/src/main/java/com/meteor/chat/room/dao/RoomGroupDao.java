package com.meteor.chat.room.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.room.domain.entity.RoomGroup;
import com.meteor.chat.room.mapper.RoomGroupMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {

    public RoomGroup getByRoomId(Long roomId) {
        return lambdaQuery().eq(RoomGroup::getRoomId, roomId)
                .one();
    }

    public List<RoomGroup> listByRoomIds(List<Long> roomIds) {
        return lambdaQuery().in(RoomGroup::getRoomId, roomIds)
                .list();
    }
}
