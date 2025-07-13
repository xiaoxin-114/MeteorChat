package com.meteor.chat.event.listener;

import com.meteor.chat.common.domain.entity.UserApply;
import com.meteor.chat.event.NewUserApplyEvent;
import com.meteor.chat.route.service.PushService;
import com.meteor.chat.user.dao.UserApplyDao;
import com.meteor.chat.websocket.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class NewUserApplyListener {
    @Resource
    private PushService pushService;
    @Resource
    private UserApplyDao userApplyDao;
    // 向被申请好友的用户发送消息
    @EventListener(classes = NewUserApplyEvent.class)
    public void snedMsgToTarget(NewUserApplyEvent event){
        UserApply userApply = event.getUserApply();
        Long targetId = userApply.getTargetId();
        int unRead = userApplyDao.countUnRead(targetId);
        pushService.pushSingleMsg(WSAdapter.buildFriendApply(userApply.getUid(), unRead), targetId);
    }
}
