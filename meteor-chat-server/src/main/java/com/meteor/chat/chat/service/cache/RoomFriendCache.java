package com.meteor.chat.chat.service.cache;

import com.meteor.chat.chat.dao.RoomFriendDao;
import com.meteor.chat.common.cache.AbstractRedisStringCache;
import com.meteor.chat.common.constants.RedisKey;
import com.meteor.chat.common.domain.entity.RoomFriend;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RoomFriendCache extends AbstractRedisStringCache<Long, RoomFriend> {
    @Resource
    private RoomFriendDao roomFriendDao;

    @Override
    protected Long getExpireTime() {
        return 5 * 60L;
    }

    @Override
    public String getKey(Long aLong) {
        return RedisKey.getKey(RedisKey.GROUP_FRIEND_STRING, aLong);
    }

    @Override
    public Map<Long, RoomFriend> load(List<Long> roomIds) {
        List<RoomFriend> roomFriends = roomFriendDao.listByRoomIds(roomIds);
        return roomFriends.stream().collect(Collectors.toMap(RoomFriend::getRoomId, Function.identity()));
    }
}
