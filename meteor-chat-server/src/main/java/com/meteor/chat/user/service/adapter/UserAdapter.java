package com.meteor.chat.user.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.vo.UserInfoVO;

public class UserAdapter {

    /**
     * 构建用户的详细信息
     * @param userInfo 用户信息
     * @param countByValidItemId 剩余改名次数
     * @return
     */
    public static UserInfoVO buildUserInfoResp(User userInfo, Integer countByValidItemId) {
        UserInfoVO userInfoResp = new UserInfoVO();
        BeanUtil.copyProperties(userInfo, userInfoResp);
        userInfoResp.setModifyNameChance(countByValidItemId);
        return userInfoResp;
    }
}
