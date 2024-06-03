package com.meteor.chat.chat.service.impl;

import com.meteor.chat.chat.service.cache.HotRoomCache;
import com.meteor.chat.chat.service.cache.RoomCache;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.entity.RoomFriend;
import com.meteor.chat.common.domain.enums.RoomFriendStatusEnum;
import com.meteor.chat.common.domain.enums.RoomTypeEnum;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.chat.dao.GroupMemberDao;
import com.meteor.chat.chat.dao.RoomDao;
import com.meteor.chat.chat.dao.RoomFriendDao;
import com.meteor.chat.chat.dao.RoomGroupDao;
import com.meteor.chat.chat.service.RoomService;
import com.meteor.chat.chat.service.adapter.RoomAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

    @Resource
    private RoomDao roomDao;

    @Resource
    private RoomGroupDao roomGroupDao;

    @Resource
    private RoomFriendDao roomFriendDao;

    @Resource
    private GroupMemberDao groupMemberDao;

    @Resource
    private HotRoomCache hotRoomCache;

    @Resource
    private RoomCache roomCache;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long buildSingleRoom(Long uid1, Long uid2) {
        if (uid1 == null || uid2 == null) {
            throw new BusinessException("用户id为空，创建单聊失败");
        }
        String roomKey = RoomAdapter.buildRoomKey(uid1, uid2);
        RoomFriend roomFriend = roomFriendDao.findByRoomKey(roomKey);
        if (Objects.nonNull(roomFriend)) {
            restoreRoomFriend(roomFriend);
            return roomFriend.getRoomId();
        } else {
            Room room = RoomAdapter.buildRoom(RoomTypeEnum.SINGLE);
            roomDao.save(room);
            RoomFriend newRoomFriend = RoomAdapter.buildFriendRoom(uid1, uid2, room.getId());
            roomFriendDao.save(newRoomFriend);
            return room.getId();
        }
    }

    private void restoreRoomFriend(RoomFriend roomFriend) {
        if (RoomFriendStatusEnum.FORBID.getCode() == roomFriend.getStatus()) {
            roomFriendDao.updateRoomStatus(roomFriend.getId(), RoomFriendStatusEnum.NORAML);
        }
    }
}
