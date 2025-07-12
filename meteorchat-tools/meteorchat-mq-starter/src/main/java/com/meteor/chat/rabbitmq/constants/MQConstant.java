package com.meteor.chat.rabbitmq.constants;

/**
 * @author meteor
 */
public interface MQConstant {

    /**
     * 群聊消息发送mq
     */
    String SEND_MSG_EXCHANGE = "chat_send_msg_exchange";
    String SEND_MSG_ROUTING_KEY = "chat_send_msg_routing_key";
    String SEND_MSG_QUEUE = "chat_send_msg_queue";

    /**
     * 消息推送
     */
    // todo 后续扩展，如果采用分布式，需要考虑群聊和私聊消息进入不同的队列，并且每个实例单独监听一个队列
    String PUSH_QUEUE = "websocket_push_queue";
    String PUSH_ROUTING_KEY = "websocket_push_routing_key";
    String PUSH_EXCHANGE = "websocket_push_exchange";

    /**
     * (授权完成后)登录信息mq
     */
    String LOGIN_QUEUE = "user_login_send_msg_queue";
    String LOGIN_ROUTING_KEY = "user_login_send_msg_routing_key";
    String LOGIN_EXCHANGE = "user_login_send_msg_exchange";

    /**
     * 扫码成功 信息发送mq
     */
    String SCAN_QUEUE = "user_scan_send_msg_queue";
    String SCAN_ROUTING_KEY = "user_scan_send_msg_routing_key";
    String SCAN_EXCHANGE = "user_scan_send_msg_exchange";
}
