package com.meteor.chat.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.RoomGroup;
import com.meteor.chat.common.domain.enums.DeleteStatusEunm;
import com.meteor.chat.common.mapper.RoomGroupMapper;
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
