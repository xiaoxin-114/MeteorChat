package com.meteor.chat.room.api;

import com.meteor.chat.api.room.RoomCommonApi;
import com.meteor.chat.api.room.dto.RoomFriendDTO;
import com.meteor.chat.api.room.dto.RoomInfoDTO;
import com.meteor.chat.api.room.dto.SingleRoomDTO;
import com.meteor.chat.room.domain.entity.Room;
import com.meteor.chat.room.service.RoomService;
import com.meteor.chat.room.service.cache.RoomCache;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;
@RestController
public class RoomCommonApiImpl implements RoomCommonApi {

    @Resource
    private RoomService roomService;
    @Resource
    private RoomCache roomCache;

    @Override
    public Long buildSingleRoom(SingleRoomDTO req) {
        return roomService.buildSingleRoom(req.getUid1(), req.getUid2());
    }

    @Override
    public void disableSingleRoom(SingleRoomDTO req) {
        roomService.disableSingleRoom(req.getUid1(), req.getUid2());
    }

    @Override
    public RoomInfoDTO getRoomInfo(Long roomId) {
        Room room = roomCache.get(roomId);
        if (Objects.isNull(room)) {
            return null;
        }
        return RoomInfoDTO.builder()
                .roomId(room.getId())
                .type(room.getType())
                .hotFlag(room.getHotFlag())
                .build();
    }

    @Override
    public Boolean hasRoomPower(Long roomId, Long uid) {
        return roomService.hasRoomPower(roomId, uid);
    }

    @Override
    public RoomFriendDTO getRoomFriend(Long roomId) {
        return roomService.getRoomFriendInfo(roomId);
    }
}
