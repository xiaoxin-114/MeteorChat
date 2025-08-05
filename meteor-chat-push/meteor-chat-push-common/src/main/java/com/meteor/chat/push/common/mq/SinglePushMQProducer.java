package com.meteor.chat.push.common.mq;

import cn.hutool.core.collection.CollectionUtil;
import com.meteor.chat.push.common.domain.dto.PushMessageDTO;
import com.meteor.chat.rabbitmq.core.constants.MQConstant;
import com.meteor.chat.rabbitmq.core.producer.MQProducer;
import com.meteor.chat.redis.core.constants.RedisKey;
import com.meteor.chat.redis.core.util.RedisUtils;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;

/**
 * 对mq进行封装，发送消息如果需要精准推送
 * 则通过接收消息的用户id去redis查询对应的websocket实例id
 * 修改routingkey，保证消息发送到对应的 实例
 */
@RequiredArgsConstructor
public class SinglePushMQProducer {
    private final MQProducer mqProducer;

    public void sendSingleMsg(String exchange, String routingKey, Object body, Long uid) {
        if (routingKey.contains(MQConstant.INSTANCE_ID_PLACE)) {
            // 根据uid找到登陆用户所在的websocket模块，实现精准推送
            List<String> instanceIds = RedisUtils.lGet(RedisKey.getKey(RedisKey.USER_CONNECT_QUEUE, uid), 0, -1);
            if (CollectionUtil.isEmpty(instanceIds)) {
                return;
            }
            HashSet<String> set = new HashSet<>(instanceIds);
            set.forEach(s -> {
                mqProducer.sendMsg(exchange, routingKey.replace("${instanceId}", s), body);
            });
        } else {
            mqProducer.sendMsg(exchange, routingKey, body);
        }
    }

    public void sendSecureSingleMsg(String exchange, String routingKey, Object body, Long uid) {
        if (routingKey.contains(MQConstant.INSTANCE_ID_PLACE)) {
            // 根据uid找到登陆用户所在的websocket模块，实现精准推送
            List<String> instanceIds = RedisUtils.lGet(RedisKey.getKey(RedisKey.USER_CONNECT_QUEUE, uid), 0, -1);
            if (CollectionUtil.isEmpty(instanceIds)) {
                return;
            }
            HashSet<String> set = new HashSet<>(instanceIds);
            set.forEach(s -> {
                mqProducer.sendSecureMsg(exchange, routingKey.replace("${instanceId}", s), body);
            });
        } else {
            mqProducer.sendSecureMsg(exchange, routingKey, body);
        }
    }
}
