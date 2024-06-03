package com.meteor.chat.msg.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.mapper.MessageMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Objects;

@Repository
public class MessageDao extends ServiceImpl<MessageMapper, Message> {
    public int countUnReadMsg(Long roomId, Date readTime) {
        return lambdaQuery().eq(Message::getRoomId, roomId)
                .eq(Objects.nonNull(readTime), Message::getCreateTime, readTime)
                .count();
    }
}
