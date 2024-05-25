package com.meteor.chat.msg.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.entity.UserFriend;
import com.meteor.chat.common.domain.enums.HotFlagEunm;
import com.meteor.chat.common.mapper.RoomMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomDao extends ServiceImpl<RoomMapper, Room> {
    /**
     * 获取热点群聊信息
     * @return
     */
    public List<Room> getHotRoom() {
        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Room::getHotFlag, HotFlagEunm.HOT_ROOM.getCode());
        return list(queryWrapper);
    }
}
