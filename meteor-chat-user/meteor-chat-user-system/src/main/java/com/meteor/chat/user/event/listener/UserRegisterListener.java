package com.meteor.chat.user.event.listener;

import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.user.domain.entity.ItemConfig;
import com.meteor.chat.user.domain.entity.User;
import com.meteor.chat.user.enums.IdempotenceCodeEnum;
import com.meteor.chat.user.enums.ItemConfigTypeEnum;
import com.meteor.chat.user.event.UserRegisterEvent;
import com.meteor.chat.user.service.UserBackpackService;
import com.meteor.chat.user.service.cache.ItemCache;
import com.meteor.chat.user.utils.IdempotentUtils;
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
        String idempotent = IdempotentUtils.getIdempotent(item.get(0).getId(), IdempotenceCodeEnum.UID, user.getId().toString());
        userBackpackService.issueItem(user.getId(), item.get(0).getId(), idempotent);
    }
}
