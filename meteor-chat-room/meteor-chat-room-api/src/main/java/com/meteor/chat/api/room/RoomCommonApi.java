package com.meteor.chat.api.room;

import com.meteor.chat.api.room.constants.ApiConstants;
import com.meteor.chat.api.room.dto.RoomFriendDTO;
import com.meteor.chat.api.room.dto.RoomInfoDTO;
import com.meteor.chat.api.room.dto.SingleRoomDTO;
import com.meteor.chat.common.result.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 聊天室信息查询api
 */
@FeignClient(name = ApiConstants.APPLICATION_NAME)
public interface RoomCommonApi {

    /**
     * 创建单聊房间
     * @return 房间id
     */
    @PostMapping(ApiConstants.ROOM_PREFIX + "/single")
    ApiResult<Long> buildSingleRoom(@RequestBody SingleRoomDTO req);

    /**
     * 禁用单聊群组
     */
    @PostMapping(ApiConstants.ROOM_PREFIX + "/single/disable")
    void disableSingleRoom(@RequestBody SingleRoomDTO req);

    /**
     * 获取房间信息
     * @param roomId 房间id
     */
    @GetMapping(ApiConstants.ROOM_PREFIX)
    ApiResult<RoomInfoDTO> getRoomInfo(@RequestParam("roomId") Long roomId);

    /**
     * 判断用户是否有为聊天群管理员
     * @param roomId 房间id
     * @param uid 用户 id
     * @return true:有管理员权限
     */
    @GetMapping(ApiConstants.ROOM_PREFIX + "/power")
    ApiResult<Boolean> hasRoomPower(@RequestParam("roomId") Long roomId, @RequestParam("uid") Long uid);

    /**
     * 获取单聊，聊天室
     * @param roomId 房间id
     */
    @GetMapping(ApiConstants.ROOM_PREFIX + "/friend")
    ApiResult<RoomFriendDTO> getRoomFriend(@RequestParam("roomId") Long roomId);
}