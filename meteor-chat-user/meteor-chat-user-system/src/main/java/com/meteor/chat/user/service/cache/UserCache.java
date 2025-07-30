package com.meteor.chat.user.service.cache;

import com.meteor.chat.redis.core.constants.RedisKey;
import com.meteor.chat.redis.core.util.RedisUtils;
import com.meteor.chat.user.domain.entity.Black;
import com.meteor.chat.user.domain.entity.User;
import com.meteor.chat.user.dao.BlackDao;
import com.meteor.chat.user.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
    private UserSummaryCache userSummaryCache;

    @Resource
    private BlackDao blackDao;

    /**
     * @return 获取在线总人数
     */
    public Long getOnlineNum() {
        String key = onlineOrOfflineKey(true);
        return RedisUtils.zCard(key);
    }

    /**
     * @return 获取离线总人数
     */
    public Long getOfflineNum() {
        String key = onlineOrOfflineKey(false);
        return RedisUtils.zCard(key);
    }

    public Set<String> getOnlineUidList() {
        String key = onlineOrOfflineKey(true);
        return RedisUtils.zAll(key);
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
     * 修改用户信息，删除缓存
     * 采用延时双删，保证缓存与数据库一致性
     * @param user 用户
     */
    public void updateUser(User user) {
        RedisUtils.del(RedisKey.getKey(RedisKey.USER_INFO_STRING, user.getId()));
        userDao.updateById(user);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        RedisUtils.del(RedisKey.getKey(RedisKey.USER_INFO_STRING, user.getId()));
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
        // 必须对于idList去重，不然会有两个重复的元素，两个同样的key也会从redis中读取出两条一样的数据
        // 导致后续出错
        List<String> keys = idList.stream()
                .distinct()
                .map(id -> RedisKey.getKey(RedisKey.USER_INFO_STRING, id))
                .collect(Collectors.toList());
        // 从redis中获取用户信息数据
        List<User> userList = RedisUtils.mget(keys, User.class);
        Map<Long, User> userMap = userList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(User::getId, Function.identity()));
        // 过滤出那些需要查询，但是redis中没有的用户数据id
        List<Long> extraUid = idList.stream().filter(id -> !userMap.containsKey(id)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(extraUid)) {
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
     * 移除用户，将用户从redis中的在线和离线列表中都删去
     * @param uid 用户id
     */
    public void remove(Long uid) {
        String onlineKey = onlineOrOfflineKey(true);
        String offlineKey = onlineOrOfflineKey(false);
        RedisUtils.zRemove(offlineKey, uid);
        RedisUtils.zRemove(onlineKey, uid);
    }

    public List<Long> getUserModifyTime(List<Long> uidList) {
        if (CollectionUtils.isEmpty(uidList)) {
            return null;
        }
        List<String> keyList = uidList.stream().map(id -> RedisKey.getKey(RedisKey.USER_MODIFY_STRING, id)).collect(Collectors.toList());
        return RedisUtils.mget(keyList, Long.class);
    }

    public void updateUserModifyTime(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_MODIFY_STRING, uid);
        RedisUtils.set(key, System.currentTimeMillis());
    }

    /**
     * 用户信息发送变化，删掉缓存，确保下次读取的是最新数据
     * @param uid
     */
    public void userInfoChange(Long uid) {
        delUserInfoChange(uid);
        userSummaryCache.delete(uid);
        updateUserModifyTime(uid);
    }

    public void delUserInfoChange(Long uid) {
        RedisUtils.del(RedisKey.getKey(RedisKey.USER_INFO_STRING, uid));
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

    @Cacheable(value = "user", key = "'blackMap'")
    public Map<Integer, Set<String>> getBlackMap() {
        List<Black> blackList = blackDao.list();
        if (CollectionUtils.isEmpty(blackList)) {
            return new HashMap<>();
        }
        Map<Integer, List<Black>> map = blackList.stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>();
        map.forEach((key, list) -> result.put(key,
                list.stream().map(Black::getTarget).collect(Collectors.toSet())));
        return result;
    }

    @Cacheable(value = "user", key = "'blackList'")
    public Set<String> getBlackList() {
        List<Black> blackList = blackDao.list();
        if (CollectionUtils.isEmpty(blackList)) {
            return new HashSet<>();
        }
        return blackList.stream().map(Black::getTarget).collect(Collectors.toSet());
    }

    @Caching(evict = {@CacheEvict(value = "user", key = "'blackMap'"), @CacheEvict(value = "user", key = "'blackList'")})
    public void clearBlackMap() {

    }
}
