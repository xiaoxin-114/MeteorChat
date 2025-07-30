package com.meteor.chat.msg.service.handler.msgmark;

import com.meteor.chat.msg.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class UnLikeMarkHandler extends AbstractMsgMarkHandler{

    @Override
    public Integer getType() {
        return MessageMarkTypeEnum.UNLIKE.getCode();
    }

    @Override
    public Integer getAnotherType() {
        return MessageMarkTypeEnum.LIKE.getCode();
    }
}
