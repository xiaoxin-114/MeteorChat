package com.meteor.chat.msg.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.enums.YesOrNoEnum;
import com.meteor.chat.msg.domain.entity.MessageMark;
import com.meteor.chat.msg.enums.MessageMarkTypeEnum;
import com.meteor.chat.msg.mapper.MessageMarkMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> {

    public long countMsgLike(Long msgId) {
        return countMsgType(msgId, MessageMarkTypeEnum.LIKE.getCode());
    }

    public long countMsgUnLike(Long msgId) {
        return countMsgType(msgId, MessageMarkTypeEnum.UNLIKE.getCode());
    }

    public long countMsgType(Long msgId, Integer type) {
        return lambdaQuery().eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getType, type)
                .eq(MessageMark::getStatus, YesOrNoEnum.YES.getCode())
                .count();
    }

    public List<MessageMark> listByMsgId(Long msgId) {
        return lambdaQuery().eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getStatus, YesOrNoEnum.YES.getCode())
                .list();
    }

    public List<MessageMark> listByMsgIdList(List<Long> msgIds) {
        return lambdaQuery().in(MessageMark::getMsgId, msgIds)
                .eq(MessageMark::getStatus, YesOrNoEnum.YES.getCode()).list();
    }

    /**
     * 根据用户id，消息id，标记类型获取一个对象
     * @param markType
     * @param msgId
     * @param uid
     * @return
     */
    public MessageMark getByTypeAndMsgIdAndUid(Integer markType, Long msgId, Long uid) {
        return lambdaQuery().eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getUid, uid)
                .eq(MessageMark::getType, markType)
                .one();
    }
}
