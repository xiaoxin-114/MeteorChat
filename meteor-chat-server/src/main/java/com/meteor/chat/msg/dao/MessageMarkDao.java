package com.meteor.chat.msg.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.MessageMark;
import com.meteor.chat.common.domain.enums.MessageMarkTypeEnum;
import com.meteor.chat.common.domain.enums.YesOrNoEnum;
import com.meteor.chat.common.mapper.MessageMapper;
import com.meteor.chat.common.mapper.MessageMarkMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> {

    public Integer countMsgLike(Long msgId) {
        return countMsgType(msgId, MessageMarkTypeEnum.LIKE.getCode());
    }

    public Integer countMsgUnLike(Long msgId) {
        return countMsgType(msgId, MessageMarkTypeEnum.UNLIKE.getCode());
    }

    private Integer countMsgType(Long msgId, Integer type) {
        return lambdaQuery().eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getType, type)
                .eq(MessageMark::getStatus, YesOrNoEnum.NO.getCode())
                .count();
    }

    public List<MessageMark> listByMsgId(Long msgId) {
        return lambdaQuery().eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getStatus, YesOrNoEnum.NO.getCode())
                .list();
    }

    public List<MessageMark> listByMsgIdList(List<Long> msgIds) {
        return lambdaQuery().in(MessageMark::getMsgId, msgIds)
                .eq(MessageMark::getStatus, YesOrNoEnum.NO.getCode()).list();
    }
}
