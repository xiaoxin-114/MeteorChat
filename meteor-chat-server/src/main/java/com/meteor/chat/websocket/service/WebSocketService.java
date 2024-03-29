package com.meteor.chat.websocket.service;


import io.netty.channel.Channel;

public interface WebSocketService {

    void handleLoginReq(Channel channel);
}
