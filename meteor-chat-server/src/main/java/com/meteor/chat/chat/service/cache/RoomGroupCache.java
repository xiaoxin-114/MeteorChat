package com.meteor.chat.chat.service.cache;

import com.meteor.chat.chat.dao.RoomGroupDao;
import com.meteor.chat.common.cache.AbstractRedisStringCache;
import com.meteor.chat.common.constants.RedisKey;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.entity.RoomGroup;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RoomGroupCache extends AbstractRedisStringCache<Long, RoomGroup> {
    private final long expireTime = 5 * 60L;
    @Resource
    private RoomGroupDao roomGroupDao;

    @Override
    protected Long getExpireTime() {
        return expireTime;
    }

    @Override
    public String getKey(Long aLong) {
        return RedisKey.getKey(RedisKey.GROUP_INFO_STRING, aLong);
    }

    @Override
    public Map<Long, RoomGroup> load(List<Long> list) {
        List<RoomGroup> roomGroups = roomGroupDao.listByIds(list);
        return roomGroups.stream().collect(Collectors.toMap(RoomGroup::getId, Function.identity()));
    }
}
