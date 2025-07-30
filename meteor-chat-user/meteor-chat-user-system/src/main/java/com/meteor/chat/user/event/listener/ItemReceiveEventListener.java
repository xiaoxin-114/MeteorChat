package com.meteor.chat.user.event.listener;

import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.domain.entity.ItemConfig;
import com.meteor.chat.user.domain.entity.UserBackpack;
import com.meteor.chat.user.enums.ItemConfigTypeEnum;
import com.meteor.chat.user.event.ItemReceiveEvent;
import com.meteor.chat.user.mapper.ItemConfigMapper;
import com.meteor.chat.user.service.cache.UserCache;
import com.meteor.chat.user.service.cache.UserInfoCache;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class ItemReceiveEventListener {

    @Resource
    private ItemConfigMapper itemConfigMapper;
    @Resource
    private UserDao userDao;
    @Resource
    private UserInfoCache userInfoCache;
    @Resource
    private UserCache userCache;

    @EventListener(value = ItemReceiveEvent.class)
    public void userReceiveItem(ItemReceiveEvent event){
        UserBackpack userBackpack = event.getUserBackpack();
        if (userBackpack == null) {
            return;
        }
        // 如果是用户收到徽章，默认自动帮用户佩戴
        ItemConfig itemConfig = itemConfigMapper.selectById(userBackpack.getItemId());
        if (ItemConfigTypeEnum.BADGE.getType().equals(itemConfig.getType())) {
            userDao.wearBadge(userBackpack.getUid(), userBackpack.getItemId());
            userCache.userInfoChange(userBackpack.getUid());
        }
    }
}
