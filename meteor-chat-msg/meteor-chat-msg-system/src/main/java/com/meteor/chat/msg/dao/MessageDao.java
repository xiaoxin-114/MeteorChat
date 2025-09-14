package com.meteor.chat.msg.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.msg.domain.entity.Message;
import com.meteor.chat.msg.domain.vo.MessageCursorReq;
import com.meteor.chat.msg.mapper.MessageMapper;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.redis.core.util.CursorUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Objects;

@Repository
public class MessageDao extends ServiceImpl<MessageMapper, Message> {
    public long countUnReadMsg(Long roomId, Date readTime) {
        return lambdaQuery().eq(Message::getRoomId, roomId)
                .eq(Objects.nonNull(readTime), Message::getCreateTime, readTime)
                .count();
    }

    public CursorPageBaseResp<Message> cursorMessage(MessageCursorReq req, Long roomId, Long lastMsgId) {
        return CursorUtils.cursorPage(req, this,
                (lambdaQuery) ->
                        lambdaQuery.eq(Message::getRoomId, roomId)
                        // 如果用户不在群聊了，只能显示历史消息，不能显示最新消息
                        .le(Objects.nonNull(lastMsgId), Message::getId, lastMsgId),
                Message::getId);
    }

    /**
     * 计算消息与回复消息之间的间隔数量，不能简单的消息id之间相减
     * 需要查询数据库中真实的这两条消息之间的消息，因为很可能有消息被删除了
     * @param roomId 房间id
     * @param msgId 消息id
     * @param replyMsgId 被回复消息的id
     * @return
     */
    public long countMsgGap(Long roomId, Long msgId, Long replyMsgId) {
        return lambdaQuery().eq(Message::getRoomId, roomId)
                .gt(Message::getId, replyMsgId)
                .lt(Message::getId, msgId)
                .count();
    }

    public void removeByRoomId(Long roomId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getRoomId, roomId);
        this.remove(queryWrapper);
    }
}
