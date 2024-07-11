package com.meteor.chat.chat.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.enums.HotFlagEunm;
import com.meteor.chat.common.mapper.RoomMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class RoomDao extends ServiceImpl<RoomMapper, Room> {
    /**
     * 获取热点群聊信息
     * @return
     */
    public List<Room> getHotRoom() {
        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Room::getHotFlag, HotFlagEunm.HOT_ROOM.getCode())
                .orderByDesc(Room::getActiveTime);
        return list(queryWrapper);
    }

    /**
     * 更新房间的最新活跃事件和最后消息id
     */
    public void refreshActiveTime(Long roomId, Date msgCreateTime, Long msgId) {
        lambdaUpdate().set(Room::getActiveTime, msgCreateTime)
                .set(Room::getLastMsgId, msgId)
                .eq(Room::getId, roomId)
                // 保证更新的是最新的消息id，防止之前发但是后处理的消息，覆盖了后方先处理的消息
                .lt(Room::getLastMsgId, msgId)
                .update();
    }
}
