package com.meteor.chat.rabbitmq.core.constants;

/**
 * @author meteor
 */
public interface MQConstant {

    String INSTANCE_ID_PLACE = "${instanceId}";

    /**
     * 群聊消息发送mq
     */
    String SEND_MSG_EXCHANGE = "chat.send.msg.exchange";
    String SEND_MSG_ROUTING_KEY = "chat.send.msg.routing.key";
    String SEND_MSG_QUEUE = "chat.send.msg.queue";

    /**
     * 消息推送
     */
    // todo 后续扩展，如果采用分布式，需要考虑群聊和私聊消息进入不同的队列，并且每个实例单独监听一个队列
    String MSG_PUSH_QUEUE = "websocket.push.queue." + INSTANCE_ID_PLACE;
    String SINGLE_PUSH_ROUTING_KEY = "websocket.single.push.routing.key." + INSTANCE_ID_PLACE;
    String SINGLE_PUSH_EXCHANGE = "websocket.single.push.exchange";
    String ROOM_PUSH_EXCHANGE = "websocket.room.push.exchange";
    /**
     * (授权完成后)登录信息mq
     */
    String LOGIN_QUEUE = "user.login.send.msg.queue." + INSTANCE_ID_PLACE;
    String LOGIN_ROUTING_KEY = "user.login.send.msg.routing.key." + INSTANCE_ID_PLACE;
    String LOGIN_EXCHANGE = "user.login.send.msg.exchange";

    /**
     * 扫码成功 信息发送mq
     */
    String SCAN_QUEUE = "user.scan.send.msg.queue." + INSTANCE_ID_PLACE;
    String SCAN_ROUTING_KEY = "user.scan.send.msg.routing.key." + INSTANCE_ID_PLACE;
    String SCAN_EXCHANGE = "user.scan.send.msg.exchange";
}
