package com.meteor.chat.msg.service.handler.msgmark;

import com.meteor.chat.common.domain.dto.MsgMarkDTO;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.MessageMark;
import com.meteor.chat.common.domain.enums.MessageMarkActTypeEnum;
import com.meteor.chat.common.domain.enums.MessageTypeEnum;
import com.meteor.chat.common.domain.enums.YesOrNoEnum;
import com.meteor.chat.common.domain.vo.req.MsgMarkReq;
import com.meteor.chat.event.MsgMarkEvent;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.msg.dao.MessageMarkDao;
import org.junit.Assert;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractMsgMarkHandler {
    @Resource
    protected MessageMarkDao messageMarkDao;
    @Resource
    private MessageDao messageDao;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public final void init() {
        MsgMarkHandlerFacroty.register(getType(), this);
    }

    @Transactional(rollbackFor = Exception.class)
    public void doMark(MsgMarkReq req, Long uid) {
        // 基础的校验
        commCheck(req);
        MessageMark oldMark = messageMarkDao.getByTypeAndMsgIdAndUid(req.getMarkType(), req.getMsgId(), uid);
        // 如果是生效的请求
        if (YesOrNoEnum.YES.getCode() == req.getActType()) {
            // 判断并取消另外一种标记类型
            cancelAnother(req.getMsgId(), uid);
        } else {
            if (Objects.isNull(oldMark)) {
                return;
            }
        }
        // 保存当前记录
        saveMessageMark(req, uid, Optional.ofNullable(oldMark).map(MessageMark::getId).orElse(null));
    }

    /**
     * 取消另外一种类型的标记
     * @param msgId 消息id
     * @param uid 用户id
     */
    protected void cancelAnother(Long msgId, Long uid){
        Integer type = getAnotherType();
        MessageMark oldMark = messageMarkDao.getByTypeAndMsgIdAndUid(type, msgId, uid);
        // 先查询数据库，如果不存在记录或者记录本身就是取消状态，无需处理
        if (Objects.isNull(oldMark) || YesOrNoEnum.NO.getCode() == oldMark.getStatus()) {
            return;
        }
        MessageMark messageMark = MessageMark.builder()
                .id(oldMark.getId())
                .status(YesOrNoEnum.NO.getCode())
                .build();
        boolean success = messageMarkDao.updateById(messageMark);
        if (success) {
            applicationEventPublisher.publishEvent(new MsgMarkEvent(this, new MsgMarkDTO(type, YesOrNoEnum.NO.getCode(), msgId, uid)));
        }
    }

    /**
     * 保存/修改标记数据
     * @param req 包含标记类型，操作类型和消息id
     * @param uid 用户id
     * @param oldId 数据库存在的标记id
     */
    private void saveMessageMark(MsgMarkReq req, Long uid, Long oldId) {
        MessageMark messageMark = MessageMark.builder()
                .id(oldId)
                .type(req.getMarkType())
                .status(YesOrNoEnum.toStatus(1 == req.getActType()))
                .msgId(req.getMsgId())
                .uid(uid)
                .build();
        boolean isSuccess = messageMarkDao.saveOrUpdate(messageMark);
        if (isSuccess) {
            applicationEventPublisher.publishEvent(new MsgMarkEvent(this, new MsgMarkDTO(req, uid)));
        }
    }

    protected void commCheck(MsgMarkReq req) {
        Long msgId = req.getMsgId();
        Message message = messageDao.getById(msgId);
        Assert.assertNotNull("消息不存在", message);
        Assert.assertFalse("消息已经被撤回，无法标记", MessageTypeEnum.RECALL.getType().equals(message.getType()));
    }

    /**
     * 获取处理器类型，点赞/踩
     * @return 1表示点赞，2表示踩
     */
    public abstract Integer getType();
    /**
     * 获取处理器类型的另一个，点赞/踩
     * @return 1表示点赞，2表示踩
     */
    public abstract Integer getAnotherType();

}
