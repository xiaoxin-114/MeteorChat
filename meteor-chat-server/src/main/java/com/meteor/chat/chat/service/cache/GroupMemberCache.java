package com.meteor.chat.chat.service.cache;

import com.meteor.chat.chat.dao.GroupMemberDao;
import com.meteor.chat.chat.dao.RoomGroupDao;
import com.meteor.chat.common.domain.entity.GroupMember;
import com.meteor.chat.common.domain.entity.RoomGroup;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GroupMemberCache {

    @Resource
    private GroupMemberDao groupMemberDao;

    @Resource
    private RoomGroupDao roomGroupDao;

    @Cacheable(cacheNames = "member", key = "'groupMember'+#roomId")
    public List<Long> getMemberUidList(Long roomId) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        if (Objects.isNull(roomGroup)) {
            return new ArrayList<>();
        }
        return groupMemberDao.getMemberUidList(roomGroup.getId());
    }

    @Caching(evict = {@CacheEvict(cacheNames = "member", key = "'groupMember'+#roomId"),
            @CacheEvict(cacheNames = "member", key = "'groupMemberMap' + #roomId")})
    public List<Long> evictMemberUidList(Long roomId) {
        return null;
    }


    @Cacheable(cacheNames = "member", key = "'groupMemberMap'+#roomId")
    public Map<Long, GroupMember> getMemberList(Long roomId) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        if (Objects.isNull(roomGroup)) {
            return new HashMap<>();
        }
        List<GroupMember> groupMemberList = groupMemberDao.getMemberList(roomGroup.getId());
        return groupMemberList.stream().collect(Collectors.toMap(GroupMember::getUid, Function.identity()));
    }
}
