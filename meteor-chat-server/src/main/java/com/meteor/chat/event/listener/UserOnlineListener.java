package com.meteor.chat.event.listener;

import com.meteor.chat.common.domain.entity.IpDetail;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.enums.ChatActiveStatusEnum;
import com.meteor.chat.common.util.IPUtils;
import com.meteor.chat.event.UserOnlineEvent;
import com.meteor.chat.route.service.PushService;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.dao.UserRoleDao;
import com.meteor.chat.user.service.cache.UserCache;
import com.meteor.chat.websocket.adapter.WSAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户登陆事件的监听器
 */
@Slf4j
@Component
public class UserOnlineListener {
    @Resource
    private UserDao userDao;
    @Resource
    private UserCache userCache;
    @Resource
    private UserRoleDao userRoleDao;
    @Resource
    private IPUtils ipUtils;
    @Resource
    private PushService pushService;

    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveRedisAndPush(UserOnlineEvent event) {
        User user = event.getUser();
        userCache.online(user.getId(), user.getLastOptTime());
        // 向所有在线用户推送，该用户登入成功的消息
        pushService.pushMsg(WSAdapter.buildUserOfflineResp(user, userCache.getOnlineNum()));
    }

    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveDB(UserOnlineEvent event) {
        //为什么不直接用user进行更新
        User user = event.getUser();
        User update = User.builder().build();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(ChatActiveStatusEnum.ONLINE.getStatus());
        String needRefreshIp = update.getIpInfo().needRefreshIp();
        if (StringUtils.isNotEmpty(needRefreshIp)) {
            try {
                IpDetail ipDetail = ipUtils.asyncGetIpDetail(needRefreshIp);
                update.getIpInfo().refreshIpDetail(ipDetail);
            } catch (Exception e) {
                log.error("ip[" + needRefreshIp + "]解析归属地异常", e);
            }
            userCache.userInfoChange(user.getId());
        }
        userDao.updateById(update);
    }
}
