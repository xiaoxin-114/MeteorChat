package com.meteor.chat.chat.service.cache;

import com.meteor.chat.chat.dao.GroupMemberDao;
import com.meteor.chat.chat.dao.RoomGroupDao;
import com.meteor.chat.common.domain.entity.RoomGroup;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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
            return null;
        }
        return groupMemberDao.getMemberList(roomGroup.getId());
    }

    @CacheEvict(cacheNames = "member", key = "'groupMember'+#roomId")
    public List<Long> evictMemberUidList(Long roomId) {
        return null;
    }
}
