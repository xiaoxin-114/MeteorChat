package com.meteor.chat.api.room;

import com.meteor.chat.api.room.constants.ApiConstants;
import com.meteor.chat.api.room.dto.ContactInfoDTO;
import com.meteor.chat.api.room.dto.MessageReadCursorPageDTO;
import com.meteor.chat.api.room.dto.ReadMessageDTO;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 聊天框共享接口
 */
@FeignClient(name = ApiConstants.APPLICATION_NAME)
public interface ContactCommonApi {

    /**
     * 获取消息已读的会话游标分页
     */
    @PostMapping(ApiConstants.CONTACT_PREFIX + "/read/page")
    ApiResult<CursorPageBaseResp<ContactInfoDTO>> cursorReadPage(@RequestBody MessageReadCursorPageDTO  req);
    /**
     * 获取消息未读的会话游标分页
     */
    @PostMapping(ApiConstants.CONTACT_PREFIX + "/unread/page")
    ApiResult<CursorPageBaseResp<ContactInfoDTO>> cursorUnReadPage(@RequestBody MessageReadCursorPageDTO  req);

    /**
     * 获取房间内的所有会话
     * @param roomId 房间id
     * @param uid 登陆用户
     * @return 获取的会话应该排除登陆用户
     */
    @GetMapping(ApiConstants.CONTACT_PREFIX + "/list")
    ApiResult<List<ContactInfoDTO>> listByRoomId(@RequestParam("roomId") Long roomId, @RequestParam("uid") Long uid);

    /**
     * 根据用户和房间号获取会话信息
     * @param roomId 房间id
     * @param uid  用户id
     */
    @GetMapping(ApiConstants.CONTACT_PREFIX)
    ApiResult<ContactInfoDTO> getByRoomIdUid(@RequestParam("roomId") Long roomId, @RequestParam("uid") Long uid);

    /**
     * 用户读取消息
     */
    @PostMapping(ApiConstants.CONTACT_PREFIX + "/read")
    void readMsg(@RequestBody ReadMessageDTO req);
}