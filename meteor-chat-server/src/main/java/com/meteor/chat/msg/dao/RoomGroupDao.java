package com.meteor.chat.msg.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.entity.RoomGroup;
import com.meteor.chat.common.domain.enums.HotFlagEunm;
import com.meteor.chat.common.mapper.RoomGroupMapper;
import com.meteor.chat.common.mapper.RoomMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {

}
