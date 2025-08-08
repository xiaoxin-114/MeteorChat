package com.meteor.chat.api.room;

import com.meteor.chat.common.result.ApiResult;

import java.util.List;

/**
 * 聊天室成员接口
 */
public interface RoomMemberCommonApi {
    /**
     * 获取聊天室成员列表
     * @param roomId 聊天室id
     */
    ApiResult<List<Long>> getMemberList(Long roomId);
}