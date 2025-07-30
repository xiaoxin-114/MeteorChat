package com.meteor.chat.msg.service.handler.msgmark;

import com.meteor.chat.msg.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class LikeMarkHandler extends AbstractMsgMarkHandler{


    @Override
    public Integer getType() {
        return MessageMarkTypeEnum.LIKE.getCode();
    }

    @Override
    public Integer getAnotherType() {
        return MessageMarkTypeEnum.UNLIKE.getCode();
    }
}
