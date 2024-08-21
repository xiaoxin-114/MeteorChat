package com.meteor.chat.route.service.impl;

import com.meteor.chat.common.constants.MQConstant;
import com.meteor.chat.common.domain.dto.PushMessageDTO;
import com.meteor.chat.consumer.ConsumerExecutor;
import com.meteor.chat.route.service.PushService;
import com.meteor.chat.websocket.domain.vo.WSBaseResp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PushServiceImpl implements PushService {

    @Resource
    private ConsumerExecutor consumerExecutor;

    @Override
    public void pushMsg(WSBaseResp<?> msg, List<Long> uidList) {
        consumerExecutor.execute(MQConstant.PUSH_TOPIC, new PushMessageDTO(msg, uidList, PushMessageDTO.NOT_ALL));
    }

    @Override
    public void pushMsg(WSBaseResp<?> msg) {
        consumerExecutor.execute(MQConstant.PUSH_TOPIC, new PushMessageDTO(msg));
    }

    @Override
    public void pushMsg(WSBaseResp<?> msg, Long uid) {
        consumerExecutor.execute(MQConstant.PUSH_TOPIC, new PushMessageDTO(msg, uid));
    }
}
