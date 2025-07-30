package com.meteor.chat.msg.service.handler.msgmark;

import com.meteor.chat.common.exception.CommonErrorEnum;
import org.junit.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MsgMarkHandlerFacroty {

    private final static Map<Integer, AbstractMsgMarkHandler> map = new ConcurrentHashMap<>();

    public static AbstractMsgMarkHandler getOrDefault(Integer type) {
        AbstractMsgMarkHandler msgMarkHandler = map.get(type);
        Assert.assertNotNull(CommonErrorEnum.PARAM_VALID.getErrMsg(), msgMarkHandler);
        return msgMarkHandler;
    }

    public static void register(Integer type, AbstractMsgMarkHandler handler) {
        map.put(type, handler);
    }
}
