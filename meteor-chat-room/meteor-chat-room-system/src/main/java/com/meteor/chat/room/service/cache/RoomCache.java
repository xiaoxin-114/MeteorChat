package com.meteor.chat.room.service.cache;

import com.meteor.chat.cache.core.AbstractRedisStringCache;
import com.meteor.chat.redis.core.constants.RedisKey;
import com.meteor.chat.room.dao.RoomDao;
import com.meteor.chat.room.domain.entity.Room;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RoomCache extends AbstractRedisStringCache<Long, Room> {
    private final long expireTime = 5 * 60L;
    @Resource
    private RoomDao roomDao;

    @Override
    protected Long getExpireTime() {
        return expireTime;
    }

    @Override
    public String getKey(Long aLong) {
        return RedisKey.getKey(RedisKey.ROOM_INFO_STRING, aLong);
    }

    @Override
    public Map<Long, Room> load(List<Long> list) {
        List<Room> rooms = roomDao.listByIds(list);
        return rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }
}
