package com.meteor.chat.user.service.cache;

import com.meteor.chat.common.constants.RedisKey;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.util.RedisUtils;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.dao.UserRoleDao;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
     * 获取用户信息，盘路缓存模式
     */
    public User getUserInfo(Long uid){
        return getUserInfoBatch(Collections.singletonList(uid)).get(uid);
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public Map<Long, User> getUserInfoBatch(List<Long> idList) {
        //获取到对于的redis中的key
        List<String> keys = idList.stream()
                .map(id -> RedisKey.getKey(RedisKey.USER_INFO_STRING, id))
                .collect(Collectors.toList());
        // 从redis中获取用户信息数据
        List<User> userList = RedisUtils.mget(keys, User.class);
        Map<Long, User> userMap = userList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(User::getId, Function.identity()));
        // 过滤出那些需要查询，但是redis中没有的用户数据id
        List<Long> extraUid = idList.stream().filter(id -> !userMap.containsKey(id)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(extraUid)) {
            // 从数据库中查询，并且存储回redis中
            List<User> users = userDao.listByIds(extraUid);
            Map<String, User> extraMap = users.stream().collect(Collectors.toMap(user -> RedisKey.getKey(RedisKey.USER_INFO_STRING, user.getId()), Function.identity()));
            RedisUtils.mset(extraMap, 5 * 60);
            // 并加入到返回结果中
            users.forEach(user -> userMap.put(user.getId(), user));
        }
        return userMap;
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
