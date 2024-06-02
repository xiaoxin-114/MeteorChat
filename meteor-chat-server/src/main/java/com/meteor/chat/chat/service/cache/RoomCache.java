package com.meteor.chat.chat.service.cache;

import com.meteor.chat.common.cache.AbstractRedisStringCache;
import com.meteor.chat.common.constants.RedisKey;
import com.meteor.chat.common.domain.entity.Room;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
public class RoomCache extends AbstractRedisStringCache<Long, Room> {
    private final long expireTime = 5 * 60L;

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
        return null;
    }
}
