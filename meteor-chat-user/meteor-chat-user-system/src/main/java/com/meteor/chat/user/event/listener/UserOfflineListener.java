package com.meteor.chat.user.event.listener;

import com.meteor.chat.push.common.core.push.PushService;
import com.meteor.chat.user.adapter.WSAdapter;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.domain.entity.User;
import com.meteor.chat.user.enums.ChatActiveStatusEnum;
import com.meteor.chat.user.event.UserOfflineEvent;
import com.meteor.chat.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class UserOfflineListener {

    @Resource
    private UserCache userCache;
    @Resource
    private UserDao userDao;
    @Resource
    private PushService pushService;

    @EventListener(classes = UserOfflineEvent.class)
    public void saveRedisAndPush(UserOfflineEvent event){
        User user = event.getUser();
        userCache.offline(user.getId(), user.getLastOptTime());
        Long onlineNum = userCache.getOnlineNum();
        // 向所有在线用户推送，该用户断开连接的消息
        pushService.pushRoomMsg(WSAdapter.buildUserOfflineResp(user, onlineNum));
    }

    @EventListener(classes = UserOfflineEvent.class)
    public void saveDB(UserOfflineEvent event){
        //为什么不直接用user进行更新
        User user = event.getUser();
        User update = User.builder().build();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
        userDao.updateById(update);

    }
}
