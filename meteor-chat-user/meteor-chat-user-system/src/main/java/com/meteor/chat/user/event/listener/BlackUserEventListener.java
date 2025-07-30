package com.meteor.chat.user.event.listener;

import com.meteor.chat.push.common.core.push.PushService;
import com.meteor.chat.push.common.domain.enums.WSRespTypeEnum;
import com.meteor.chat.push.common.domain.vo.WSBaseResp;
import com.meteor.chat.push.common.domain.vo.WSBlack;
import com.meteor.chat.user.domain.entity.User;
import com.meteor.chat.user.enums.UserStatusEnum;
import com.meteor.chat.user.event.BlackUserEvent;
import com.meteor.chat.user.service.cache.UserCache;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class BlackUserEventListener {

    @Resource
    private UserCache userCache;

    @Resource
    private PushService pushService;

    /**
     * 给所有在线用户发送该用户被拉黑的消息
     * @param event
     */
    @EventListener(BlackUserEvent.class)
    public void sendBlackMsg (BlackUserEvent event) {
        pushService.pushRoomMsg(new WSBaseResp<>(WSRespTypeEnum.BLACK.getType(),
                new WSBlack(event.getUser().getId())));
    }

    /**
     * 更改拉黑用户的状态，并且把用户从在线和离线列表中删去
     * @param event
     */
    @EventListener(BlackUserEvent.class)
    public void updateUser (BlackUserEvent event) {
        User user = event.getUser();
        user.setStatus(UserStatusEnum.BLACK.getId());
        userCache.updateUser(user);
        userCache.remove(user.getId());
    }

    /**
     * 清除黑名单缓存
     * @param event
     */
    @EventListener(BlackUserEvent.class)
    public void clearBlackCache (BlackUserEvent event) {
        userCache.clearBlackMap();
    }
}
