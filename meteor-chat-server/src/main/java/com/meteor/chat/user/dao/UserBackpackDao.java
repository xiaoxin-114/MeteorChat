package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meteor.chat.common.domain.entity.UserBackpack;

public interface UserBackpackDao extends IService<UserBackpack> {
    UserBackpack getByIdempotent(String idempotent);

    /**
     * 查询用户已有该物品的数量
     * @param uid 用户id
     * @param itemId 物品id
     */
    int getCountByUidAndItemId(Long uid, Long itemId);
}
