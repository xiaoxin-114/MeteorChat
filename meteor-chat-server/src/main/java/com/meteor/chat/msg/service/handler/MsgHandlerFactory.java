package com.meteor.chat.msg.service.handler;

import com.meteor.chat.common.domain.enums.MessageTypeEnum;
import com.meteor.chat.common.exception.CommonErrorEnum;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

public class MsgHandlerFactory {

    private static final Map<Integer, AbstractMsgHandler> STRATEGY_MAP = new HashMap<>(MessageTypeEnum.values().length);

    public static void regsiter(Integer type, AbstractMsgHandler handler) {
        STRATEGY_MAP.put(type, handler);
    }

    public static AbstractMsgHandler getStrategyOrNull(Integer code) {
        AbstractMsgHandler msgHandler = STRATEGY_MAP.get(code);
        Assert.assertNotNull(CommonErrorEnum.PARAM_VALID.getErrMsg(), msgHandler);
        return msgHandler;
    }
}
