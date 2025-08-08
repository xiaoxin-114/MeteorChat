package com.meteor.chat.msg.service.handler.msg;

import com.meteor.chat.api.user.UserInfoCommonApi;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.msg.domain.entity.Message;
import com.meteor.chat.msg.domain.entity.MsgRecall;
import com.meteor.chat.msg.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
@Slf4j
public class RecallMsgHandler extends AbstractMsgHandler<String>{
    @DubboReference
    private UserInfoCommonApi userInfoCommonApi;

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
        UserInfoDTO userInfo = userInfoCommonApi.getUserInfo(uid).getCheckData();
        if (Objects.equals(userInfo.getUid(), message.getFromUid())) {
            return userInfo.getName() + "撤回了一条消息";
        }
        return "管理员[" + userInfo.getName() + "]撤回了一条消息";
    }

    @Override
    public String replyMsgText(Message message) {
        return "[撤回消息]";
    }
}
