package com.meteor.chat.event.listener;

import com.meteor.chat.common.domain.entity.ItemConfig;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.enums.IdempotenceCodeEnum;
import com.meteor.chat.common.domain.enums.ItemConfigTypeEnum;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.common.util.CommonUtils;
import com.meteor.chat.event.UserRegisterEvent;
import com.meteor.chat.user.service.UserBackpackService;
import com.meteor.chat.user.service.cache.ItemCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class UserRegisterListener {

    @Resource
    private UserBackpackService userBackpackService;
    @Resource
    private ItemCache itemCache;

    @EventListener(classes = UserRegisterEvent.class)
    public void sendCard(UserRegisterEvent event){
        User user = event.getUser();
        List<ItemConfig> item = itemCache.getByType(ItemConfigTypeEnum.MODIFY_NAME_CARD.getType().toString());
        if (item == null || item.size() != 1 || item.get(0) == null) {
            throw new BusinessException("改名卡数据异常");
        }
        // 送一张改名卡
        String idempotent = CommonUtils.getIdempotent(item.get(0).getId(), IdempotenceCodeEnum.UID, user.getId().toString());
        userBackpackService.issueItem(user.getId(), item.get(0).getId(), idempotent);
    }
}
