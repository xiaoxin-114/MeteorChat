package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.mybatis.domain.CursorPageBaseReq;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.redis.core.util.CursorUtils;
import com.meteor.chat.user.domain.entity.User;
import com.meteor.chat.user.enums.ChatActiveStatusEnum;
import com.meteor.chat.user.enums.UserStatusEnum;
import com.meteor.chat.user.mapper.UserMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

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
                        .in(!CollectionUtils.isEmpty(uidList), User::getId, uidList), User::getLastOptTime);
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
