package com.meteor.chat.event.listener;

import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.enums.ChatActiveStatusEnum;
import com.meteor.chat.common.util.RedisUtils;
import com.meteor.chat.event.UserOfflineEvent;
import com.meteor.chat.user.dao.UserDao;
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

    @EventListener(classes = UserOfflineEvent.class)
    public void saveRedisAndPush(UserOfflineEvent event){
        User user = event.getUser();
        userCache.offline(user.getId(), user.getLastOptTime());
        //todo 向所有在线用户推送，该用户断开连接的消息
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
