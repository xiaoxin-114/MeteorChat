package com.meteor.chat.api.room;

import com.meteor.chat.api.room.constants.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 聊天室成员接口
 */
@FeignClient(name = ApiConstants.APPLICATION_NAME)
public interface RoomMemberCommonApi {
    /**
     * 获取聊天室成员列表
     * @param roomId 聊天室id
     */
    @GetMapping(ApiConstants.ROOM_MEMBER_PREFIX)
    List<Long> getMemberList(@RequestParam("roomId") Long roomId);
}