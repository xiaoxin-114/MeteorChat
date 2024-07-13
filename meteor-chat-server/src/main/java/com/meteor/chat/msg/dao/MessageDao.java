package com.meteor.chat.msg.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.MessageCursorReq;
import com.meteor.chat.common.mapper.MessageMapper;
import com.meteor.chat.common.util.CursorUtils;
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

    public CursorPageBaseResp<Message> cursorMessage(MessageCursorReq req, Long uid, Long lastMsgId) {
        return CursorUtils.cursorPage(req, this,
                (lambdaQuery) ->
                        lambdaQuery.eq(Message::getRoomId, req.getCursor())
                        // 如果用户不在群聊了，只能显示历史消息，不能显示最新消息
                        .le(Objects.nonNull(lastMsgId), Message::getId, lastMsgId),
                Message::getId);
    }
}
