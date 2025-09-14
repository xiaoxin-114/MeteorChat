package com.meteor.chat.user.service;


import com.meteor.chat.user.domain.entity.UserBackpack;
import com.meteor.chat.user.domain.vo.BadgeResp;

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

    /**
     * 获取用户改名卡数量
     * @param uid
     * @return
     */
    Long countRenameTimes(Long uid);


    /**
     * 获取徽章图鉴
     * @param uid
     * @return
     */
    List<BadgeResp> allBadgeList(Long uid);

    boolean useBackpackItem(Long renameCardId);

    /**
     * 获取用户背包中某个类型的一个物品
     * @param uid 用户id
     * @param type 物品类型
     * @return
     */
    UserBackpack getOneBackpackByItemType(Long uid, Integer type);
}
