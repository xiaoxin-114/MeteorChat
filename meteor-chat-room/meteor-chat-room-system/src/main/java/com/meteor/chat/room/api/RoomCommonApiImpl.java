package com.meteor.chat.room.api;

import com.meteor.chat.api.room.RoomCommonApi;
import com.meteor.chat.api.room.dto.RoomFriendDTO;
import com.meteor.chat.api.room.dto.RoomInfoDTO;
import com.meteor.chat.api.room.dto.SingleRoomDTO;
import com.meteor.chat.common.result.ApiResult;
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
    public ApiResult<Long> buildSingleRoom(SingleRoomDTO req) {
        return ApiResult.success(roomService.buildSingleRoom(req.getUid1(), req.getUid2()));
    }

    @Override
    public void disableSingleRoom(SingleRoomDTO req) {
        roomService.disableSingleRoom(req.getUid1(), req.getUid2());
    }

    @Override
    public ApiResult<RoomInfoDTO> getRoomInfo(Long roomId) {
        Room room = roomCache.get(roomId);
        if (Objects.isNull(room)) {
            return null;
        }
        return ApiResult.success(RoomInfoDTO.builder()
                .roomId(room.getId())
                .type(room.getType())
                .hotFlag(room.getHotFlag())
                .build());
    }

    @Override
    public ApiResult<Boolean> hasRoomPower(Long roomId, Long uid) {
        return ApiResult.success(roomService.hasRoomPower(roomId, uid));
    }

    @Override
    public ApiResult<RoomFriendDTO> getRoomFriend(Long roomId) {
        return ApiResult.success(roomService.getRoomFriendInfo(roomId));
    }
}
