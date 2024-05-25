package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.UserFriend;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.CursorPageBaseReq;
import com.meteor.chat.common.mapper.UserFriendMapper;
import com.meteor.chat.common.util.CursorUtils;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class UserFriendDao extends ServiceImpl<UserFriendMapper, UserFriend> {

    public CursorPageBaseResp<UserFriend> pageFriendList(Long uid, CursorPageBaseReq request) {
        return CursorUtils.cursorPage(request, this, wrapper -> wrapper.eq(UserFriend::getUid, uid).eq(UserFriend::getDeleteStatus, 0) , UserFriend::getId);
    }

    public UserFriend getFriend(Long uid, Long targetUid) {
        return this.getOne(
                new LambdaQueryWrapper<UserFriend>()
                        .eq(UserFriend::getUid, uid).eq(UserFriend::getFriendUid, targetUid)
        );
    }

    /**
     * 获取用户的所有好友列表
     * @param uid 用户id
     * @return
     */
    public List<UserFriend> getFriends(Long uid) {
        return list(new LambdaQueryWrapper<UserFriend>()
                .eq(UserFriend::getUid, uid)
                .eq(UserFriend::getDeleteStatus, 0));
    }

    public List<UserFriend> getFriendRelation(Long friendId, Long uid) {
        return lambdaQuery()
                .eq(UserFriend::getUid, uid)
                .eq(UserFriend::getFriendUid, friendId)
                .or()
                .eq(UserFriend::getUid, friendId)
                .eq(UserFriend::getFriendUid, uid)
                .list();
    }
}
