package com.meteor.chat.user.service;

import com.meteor.chat.common.domain.entity.UserBackpack;
import com.meteor.chat.common.domain.enums.IdempotenceCodeEnum;

import java.util.List;

public interface UserBackpackService {

    void issueItem(Long uid, Long itemId, String idempotent);

    /**
     * 根据用户id和物品id查询列表
     * @param list
     * @param itemIdList
     * @return
     */
    List<UserBackpack> listByUidAndItemId(List<Long> list, List<Long> itemIdList);
}
