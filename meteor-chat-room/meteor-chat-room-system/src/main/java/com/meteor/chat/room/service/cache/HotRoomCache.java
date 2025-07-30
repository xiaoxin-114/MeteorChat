package com.meteor.chat.room.service.cache;

import cn.hutool.core.lang.Pair;
import com.meteor.chat.redis.core.constants.RedisKey;
import com.meteor.chat.common.domain.CursorPageBaseReq;
import com.meteor.chat.common.domain.CursorPageBaseResp;
import com.meteor.chat.redis.core.util.CursorUtils;
import com.meteor.chat.redis.core.util.RedisUtils;
import com.meteor.chat.room.dao.RoomDao;
import com.meteor.chat.room.domain.entity.Room;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class HotRoomCache {

    @Resource
    private RoomDao roomDao;

    public void refreshActiveTime(Long roomId, Date activeTime) {
        RedisUtils.zAdd(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), roomId, (double) activeTime.getTime());
    }

    public CursorPageBaseResp<Pair<Long, Double>> cursorPage(CursorPageBaseReq req) {
        CursorPageBaseResp<Pair<Long, Double>> resp = CursorUtils.cursorRedisPage(req, RedisKey.getKey(RedisKey.HOT_ROOM_ZET), Long::parseLong);
        return resp;
    }

    public Set<ZSetOperations.TypedTuple<String>> rangeByScore(Double min, Double max) {
        return RedisUtils.zRangeByScoreWithScores(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), min, max);
    }

    @PostConstruct
    private void loadData() {
        String key = RedisKey.getKey(RedisKey.HOT_ROOM_ZET);
        if (!RedisUtils.hasKey(key)) {
            // 初始化热门房间
            List<Room> hotRoom = roomDao.getHotRoom();
            Set<ZSetOperations.TypedTuple<String>> tupleSet = hotRoom.stream()
                    .filter(Objects::nonNull)
                    .map(room -> {
                        DefaultTypedTuple<String> tuple = new DefaultTypedTuple<String >(room.getId().toString(), Double.parseDouble(room.getActiveTime().getTime() + ""));
                        return tuple;
                    }).collect(Collectors.toSet());
            RedisUtils.zAdd(key, tupleSet);
        }
    }

}
