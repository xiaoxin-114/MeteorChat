package com.meteor.chat.user.service.adapter;

import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.entity.UserApply;
import com.meteor.chat.common.domain.entity.UserFriend;
import com.meteor.chat.common.domain.enums.DeleteStatusEunm;
import com.meteor.chat.common.domain.enums.ReadEnum;
import com.meteor.chat.common.domain.enums.UserApplyStatusEnum;
import com.meteor.chat.common.domain.enums.UserApplyTypeEnum;
import com.meteor.chat.common.domain.vo.FriendApplyResp;
import com.meteor.chat.common.domain.vo.FriendResp;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class FriendAdapter {

    /**
     * 根据好友id列表和用户信息，构建好友用户信息列表
     * @param ids
     * @param users
     * @return
     */
    public static List<FriendResp> convertFriendRespList(List<Long> ids, Map<Long, User> users) {
        return ids.stream().map((id) -> {
            FriendResp friendResp = new FriendResp();
            friendResp.setUid(id);
            User user = users.get(id);
            if (Objects.nonNull(user)) {
                friendResp.setActiveStatus(user.getActiveStatus());
            }
            return friendResp;
        }).collect(Collectors.toList());
    }

    public static UserApply buildNewApply(Long uid, Long targetId, String msg) {
        UserApply userApply = new UserApply();
        userApply.setUid(uid);
        userApply.setTargetId(targetId);
        userApply.setMsg(msg);
        userApply.setReadStatus(ReadEnum.UNREAD.getCode());
        userApply.setStatus(UserApplyStatusEnum.WAITING.getCode());
        userApply.setType(UserApplyTypeEnum.GETFRIEND.getCode());
        return userApply;
    }

    public static List<FriendApplyResp> convertToFriendApplyList(List<UserApply> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().
                map(userApply -> {
                    FriendApplyResp applyResp = new FriendApplyResp();
                    applyResp.setApplyId(userApply.getId());
                    applyResp.setStatus(userApply.getStatus());
                    applyResp.setUid(userApply.getUid());
                    applyResp.setMsg(userApply.getMsg());
                    applyResp.setType(userApply.getType());
                    return applyResp;
                }).collect(Collectors.toList());
    }

    public static UserFriend buildUserFriend(Long uid, Long targetId) {
        UserFriend userFriend = new UserFriend();
        userFriend.setUid(uid);
        userFriend.setFriendUid(targetId);
        userFriend.setDeleteStatus(DeleteStatusEunm.NORAML.getCode());
        return userFriend;
    }
}
