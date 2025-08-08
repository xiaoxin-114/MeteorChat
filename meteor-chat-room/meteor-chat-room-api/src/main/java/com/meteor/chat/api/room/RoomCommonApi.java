package com.meteor.chat.api.room;

import com.meteor.chat.api.room.dto.RoomFriendDTO;
import com.meteor.chat.api.room.dto.RoomInfoDTO;
import com.meteor.chat.api.room.dto.SingleRoomDTO;
import com.meteor.chat.common.result.ApiResult;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 聊天室信息查询api
 */
@DubboService
public interface RoomCommonApi {

    /**
     * 创建单聊房间
     * @return 房间id
     */
    ApiResult<Long> buildSingleRoom( SingleRoomDTO req);

    /**
     * 禁用单聊群组
     */
    void disableSingleRoom(SingleRoomDTO req);

    /**
     * 获取房间信息
     * @param roomId 房间id
     */
    ApiResult<RoomInfoDTO> getRoomInfo(Long roomId);

    /**
     * 判断用户是否有为聊天群管理员
     * @param roomId 房间id
     * @param uid 用户 id
     * @return true:有管理员权限
     */
    ApiResult<Boolean> hasRoomPower(Long roomId, Long uid);

    /**
     * 获取单聊，聊天室
     * @param roomId 房间id
     */
    ApiResult<RoomFriendDTO> getRoomFriend(Long roomId);
}