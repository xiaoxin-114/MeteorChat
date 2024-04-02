package com.meteor.chat.websocket.service;


import io.netty.channel.Channel;
import me.chanjar.weixin.common.error.WxErrorException;

public interface WebSocketService {

    void handleLoginReq(Channel channel) throws WxErrorException;
}
