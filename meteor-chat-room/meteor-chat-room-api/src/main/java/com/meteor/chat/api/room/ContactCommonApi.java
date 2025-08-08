package com.meteor.chat.api.room;

import com.meteor.chat.api.room.dto.ContactInfoDTO;
import com.meteor.chat.api.room.dto.MessageReadCursorPageDTO;
import com.meteor.chat.api.room.dto.ReadMessageDTO;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import java.util.List;

/**
 * 聊天框共享接口
 */

public interface ContactCommonApi {

    /**
     * 获取消息已读的会话游标分页
     */
    ApiResult<CursorPageBaseResp<ContactInfoDTO>> cursorReadPage( MessageReadCursorPageDTO  req);
    /**
     * 获取消息未读的会话游标分页
     */
    ApiResult<CursorPageBaseResp<ContactInfoDTO>> cursorUnReadPage( MessageReadCursorPageDTO  req);

    /**
     * 获取房间内的所有会话
     * @param roomId 房间id
     * @param uid 登陆用户
     * @return 获取的会话应该排除登陆用户
     */
    ApiResult<List<ContactInfoDTO>> listByRoomId( Long roomId,  Long uid);

    /**
     * 根据用户和房间号获取会话信息
     * @param roomId 房间id
     * @param uid  用户id
     */
    ApiResult<ContactInfoDTO> getByRoomIdUid( Long roomId, Long uid);

    /**
     * 用户读取消息
     */
    void readMsg( ReadMessageDTO req);
}