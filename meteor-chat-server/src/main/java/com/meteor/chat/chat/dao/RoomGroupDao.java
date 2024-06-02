package com.meteor.chat.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.RoomGroup;
import com.meteor.chat.common.mapper.RoomGroupMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {

}
