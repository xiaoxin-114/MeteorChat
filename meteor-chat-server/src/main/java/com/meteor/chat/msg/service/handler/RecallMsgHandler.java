package com.meteor.chat.msg.service.handler;

import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.MsgRecall;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.enums.MessageTypeEnum;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class RecallMsgHandler extends AbstractMsgHandler<String>{
    @Resource
    private MessageDao messageDao;

    @Resource
    private UserCache userCache;

    @Override
    void saveMessageExtra(Message message, String body) {
        throw new UnsupportedOperationException("撤回消息不允许直接存储");
    }

    @Override
    MessageTypeEnum getMsgType() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    public String messageText(Message message) {
        return "撤回了一条消息";
    }

    @Override
    public Object buildMessageBody(Message message) {
        MsgRecall recall = message.getExtra().getRecall();
        if (Objects.isNull(recall)) {
            throw new RuntimeException("撤回消息数据异常，缺失撤回信息");
        }
        Long uid = recall.getRecallUid();
        User userInfo = userCache.getUserInfo(uid);
        if (Objects.equals(userInfo.getId(), message.getFromUid())) {
            return userInfo.getName() + "撤回了一条消息";
        }
        return "管理员[" + userInfo.getName() + "]撤回了一条消息";
    }

    @Override
    public String replyMsgText(Message message) {
        return "[撤回消息]";
    }
}
