package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.enums.ChatActiveStatusEnum;
import com.meteor.chat.common.domain.enums.UserStatusEnum;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.CursorPageBaseReq;
import com.meteor.chat.common.mapper.UserMapper;
import com.meteor.chat.common.util.CursorUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao extends ServiceImpl<UserMapper, User> {

    public User getByOpenId(String openId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>().eq(User::getOpenId, openId);
        return getOne(queryWrapper);
    }

    public List<User> getByName(String name) {
        return lambdaQuery().eq(User::getName, name).list();
    }

    public User getByUsername(String username) {
        return lambdaQuery().eq(User::getUsername, username).one();
    }

    public void rename(Long uid, String name) {
        lambdaUpdate().eq(User::getId, uid).set(User::getName, name).update();
    }

    public void wearBadge(Long uid, Long itemId) {
        lambdaUpdate().eq(User::getId, uid)
                .set(User::getItemId, itemId)
                .update();
    }

    public CursorPageBaseResp<User> cursorPage(CursorPageBaseReq req, ChatActiveStatusEnum key, List<Long> uidList) {
        return CursorUtils.cursorPage(req, this,
                lambdaQuery -> lambdaQuery.eq(User::getActiveStatus, key.getStatus())
                        .in(CollectionUtils.isNotEmpty(uidList), User::getId, uidList), User::getLastOptTime);
    }

    /**
     * 全员群查询用户列表
     * @return
     */
    public List<User> getMemberList() {
        return lambdaQuery().eq(User::getStatus, UserStatusEnum.NORMAL.getId())
                .orderByDesc(User::getLastOptTime)
                .last("limit 1000")
                .select(User::getId, User::getAvatar, User::getName)
                .list();
    }
}
