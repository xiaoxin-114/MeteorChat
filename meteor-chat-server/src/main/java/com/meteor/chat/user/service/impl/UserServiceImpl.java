package com.meteor.chat.user.service.impl;

import com.meteor.chat.common.domain.entity.IpInfo;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.enums.RoleEnum;
import com.meteor.chat.common.domain.vo.UserInfoVO;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.event.BlackUserEvent;
import com.meteor.chat.event.UserRegisterEvent;
import com.meteor.chat.user.dao.BlackDao;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.dao.UserRoleDao;
import com.meteor.chat.user.service.UserBackpackService;
import com.meteor.chat.user.service.UserService;
import com.meteor.chat.user.service.adapter.UserAdapter;
import com.meteor.chat.user.service.cache.UserCache;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private UserCache userCache;
    @Resource
    private UserBackpackService userBackpackService;
    @Resource
    private UserRoleDao userRoleDao;
    @Resource
    private BlackDao blackDao;

    @Override
    public void register(User user) {
        userDao.save(user);
        //用户注册事件推送
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, user));
    }

    @Override
    public UserInfoVO getUserInfo(Long uid) {
        User userInfo = userCache.getUserInfo(uid);
        // 背包改名卡查询，获取用户可改名次数
        int renameTimes = userBackpackService.countRenameTimes(uid);
        return UserAdapter.buildUserInfoResp(userInfo, renameTimes);
    }

    @Override
    public void wearBadge(Long uid, Long itemId) {
        User userInfo = User.builder().id(uid).itemId(itemId).build();
        userDao.updateById(userInfo);
    }

    @Override
    public boolean isAdmin(Long uid) {
        return userRoleDao.hasPower(uid, RoleEnum.SUPERADMIN.getId())
                || userRoleDao.hasPower(uid, RoleEnum.CHAT_ADMIN.getId());
    }

    @Override
    public void black(Long blackId) {
        User blackUser = userCache.getUserInfo(blackId);
        if (blackUser == null) {
            throw new BusinessException("拉黑的用户不存在");
        }
        blackDao.blackUid(blackUser.getId());
        IpInfo ipInfo = blackUser.getIpInfo();
        blackDao.blackIP(ipInfo.getCreateIp());
        // 如果ip相同不重复拉黑，以免数据库报错
        if (!ipInfo.getCreateIp().equals(ipInfo.getUpdateIp())) {
            blackDao.blackIP(ipInfo.getUpdateIp());
        }
        applicationEventPublisher.publishEvent(new BlackUserEvent(this, blackUser));
    }
}
