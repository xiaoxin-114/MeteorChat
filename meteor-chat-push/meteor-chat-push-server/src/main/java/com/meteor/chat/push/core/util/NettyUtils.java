package com.meteor.chat.push.core.util;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;


public class NettyUtils {
    public static final AttributeKey<String> TOKEN_KEY = AttributeKey.valueOf("token");
    public static final AttributeKey<String> IP_KEY = AttributeKey.valueOf("ip");
    public static final AttributeKey<Long> UID_KEY = AttributeKey.valueOf("uid");

    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey, T value){
        channel.attr(attributeKey).set(value);
    }

    public static <T> T getAttr(Channel channel, AttributeKey<T> attributeKey){
        return channel.attr(attributeKey).get();
    }
}
