package com.meteor.chat.user.event.listener;

import com.meteor.chat.push.common.core.push.PushService;
import com.meteor.chat.user.adapter.WSAdapter;
import com.meteor.chat.user.dao.UserApplyDao;
import com.meteor.chat.user.domain.entity.UserApply;
import com.meteor.chat.user.event.NewUserApplyEvent;
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
    public void sendMsgToTarget(NewUserApplyEvent event){
        UserApply userApply = event.getUserApply();
        Long targetId = userApply.getTargetId();
        int unRead = userApplyDao.countUnRead(targetId);
        pushService.pushSingleMsg(WSAdapter.buildFriendApply(userApply.getUid(), unRead), targetId);
    }
}
