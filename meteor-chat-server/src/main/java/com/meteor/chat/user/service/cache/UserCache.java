package com.meteor.chat.user.service.cache;

import com.meteor.chat.common.constants.RedisKey;
import com.meteor.chat.common.util.RedisUtils;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.dao.UserRoleDao;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 用户列表缓存，以zset数据格式，在redis中存储用户的在线列表和离线列表，
 * 以操作时间为score进行排序
 */
@Slf4j
@Component
public class UserCache {

    @Resource
    private UserDao userDao;

    @Resource
    private UserRoleDao userRoleDao;

    /**
     * 获取在线总人数
     * @return
     */
    public Long getOnlineNum() {
        String key = onlineOrOfflineKey(true);
        return RedisUtils.zCard(key);
    }

    /**
     * 获取离线总人数
     * @return
     */
    public Long getOfflineNum() {
        String key = onlineOrOfflineKey(false);
        return RedisUtils.zCard(key);
    }

    /**
     * 用户上线
     * @param uid 用户id
     * @param lastOptTime 用户的最后操作时间
     */
    public void online(Long uid, Date lastOptTime) {
        String onlineKey = onlineOrOfflineKey(true);
        String offlineKey = onlineOrOfflineKey(false);
        RedisUtils.zRemove(offlineKey, uid);
        RedisUtils.zAdd(onlineKey, uid, lastOptTime.getTime());
    }

    /**
     * 用户下线
     * @param uid 用户id
     * @param lastOptTime 用户的最后操作时间
     */
    public void offline(Long uid, Date lastOptTime) {
        String onlineKey = onlineOrOfflineKey(true);
        String offlineKey = onlineOrOfflineKey(false);
        RedisUtils.zRemove(onlineKey, uid);
        RedisUtils.zAdd(offlineKey, uid, lastOptTime.getTime());
    }

    /**
     * 判断用户是否在线
     * @param uid 用户id
     * @return true在线，false离线
     */
    public boolean isOnline(Long uid) {
        String key = onlineOrOfflineKey(true);
        return RedisUtils.zIsMember(key, uid);
    }

    /**
     * 获取在线或离线用户id列表在redis存储的key
     * @param mark true获取在线的，false获取离线的
     * @return 对应的key
     */
    private String onlineOrOfflineKey(boolean mark) {
        String key = mark ? RedisKey.ONLINE_UID_ZET : RedisKey.OFFLINE_UID_ZET;
        return RedisKey.getKey(key);
    }
}
