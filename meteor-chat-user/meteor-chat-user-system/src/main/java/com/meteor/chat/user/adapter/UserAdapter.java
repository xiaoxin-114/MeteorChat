package com.meteor.chat.user.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.user.domain.dto.SummaryInfoDTO;
import com.meteor.chat.user.domain.entity.ItemConfig;
import com.meteor.chat.user.domain.entity.User;
import com.meteor.chat.user.domain.entity.UserRole;
import com.meteor.chat.user.domain.vo.BadgeResp;
import com.meteor.chat.user.domain.vo.UserInfoVO;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserAdapter {

    /**
     * 构建用户的详细信息
     * @param userInfo 用户信息
     * @param countByValidItemId 剩余改名次数
     * @return
     */
    public static UserInfoVO buildUserInfoResp(User userInfo, long countByValidItemId) {
        UserInfoVO userInfoResp = new UserInfoVO();
        BeanUtil.copyProperties(userInfo, userInfoResp);
        userInfoResp.setModifyNameChance(countByValidItemId);
        return userInfoResp;
    }

    public static List<BadgeResp> buildBadgeResp(List<ItemConfig> badges, SummaryInfoDTO userDto) {
        if (CollectionUtils.isEmpty(badges)) {
            return new ArrayList<>();
        }
        List<Long> haveBadgeIds = Optional.of(userDto).map(SummaryInfoDTO::getItemIds).orElse(new ArrayList<>());
        return badges.stream().map(badge -> {
            BadgeResp resp = new BadgeResp();
            resp.setId(badge.getId());
            resp.setImg(badge.getImg());
            resp.setDescribe(badge.getDescribe());
            resp.setObtain(haveBadgeIds.contains(badge.getId()) ? 1 : 0);
            resp.setWearing(Objects.equals(badge.getId(), userDto.getWearingItemId()) ? 1 : 0);
            return resp;
        }).collect(Collectors.toList());
    }

    public static UserInfoDTO buildUserInfoDTO(User userInfo, UserRole role) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtil.copyProperties(userInfo, userInfoDTO);
        userInfoDTO.setUid(userInfo.getId());
        userInfoDTO.setRoleId(Optional.ofNullable(role).map(UserRole::getRoleId).orElse(null));
        return userInfoDTO;
    }
}
