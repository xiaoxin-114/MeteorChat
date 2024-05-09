package com.meteor.chat.event.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meteor.chat.common.domain.entity.ItemConfig;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.enums.IdempotenceCodeEnum;
import com.meteor.chat.common.domain.enums.ItemConfigTypeEnum;
import com.meteor.chat.common.util.CommonUtils;
import com.meteor.chat.event.UserRegisterEvent;
import com.meteor.chat.user.dao.ItemConfigDao;
import com.meteor.chat.user.service.UserBackpackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class UserRegisterListener {

    @Resource
    private UserBackpackService userBackpackService;
    @Resource
    private ItemConfigDao itemConfigDao;

    @EventListener(classes = UserRegisterEvent.class)
    public void sendCard(UserRegisterEvent event){
        User user = event.getUser();
        ItemConfig item = itemConfigDao.getOne(
                new LambdaQueryWrapper<ItemConfig>()
                        .eq(ItemConfig::getType, ItemConfigTypeEnum.MODIFY_NAME_CARD.getType()));
        // 送一张改名卡
        String idempotent = CommonUtils.getIdempotent(item.getId(), IdempotenceCodeEnum.UID, user.getId().toString());
        userBackpackService.issueItem(user.getId(), item.getId(), idempotent);
    }
}
