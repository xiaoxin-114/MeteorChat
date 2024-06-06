package com.meteor.chat.event.listener;

import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.enums.UserStatusEnum;
import com.meteor.chat.event.BlackUserEvent;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.service.cache.UserCache;
import com.meteor.chat.websocket.domain.enums.WSRespTypeEnum;
import com.meteor.chat.websocket.domain.vo.WSBaseResp;
import com.meteor.chat.websocket.domain.vo.WSBlack;
import com.meteor.chat.websocket.service.WebSocketService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class BlackUserEventListener {

    @Resource
    private UserCache userCache;

    @Resource
    private WebSocketService webSocketService;

    /**
     * 给所有在线用户发送该用户被拉黑的消息
     * @param event
     */
    @EventListener(BlackUserEvent.class)
    public void sendBlackMsg (BlackUserEvent event) {
        webSocketService.sendToAllOnline(
                new WSBaseResp<>(WSRespTypeEnum.BLACK.getType(),
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
