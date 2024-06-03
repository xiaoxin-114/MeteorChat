package com.meteor.chat.chat.controller;

import com.meteor.chat.chat.service.ContactService;
import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.domain.vo.ChatRoomResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.ContactFriendReq;
import com.meteor.chat.common.domain.vo.req.CursorPageBaseReq;
import com.meteor.chat.common.domain.vo.req.IdBaseReq;
import com.meteor.chat.common.util.UserContext;
import com.meteor.chat.chat.service.RoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/capi/chat/public/contact")
@ApiOperation("会话请求")
public class ContactController {

    @Resource
    private RoomService roomService;

    @Resource
    private ContactService contactService;

    @GetMapping("/page")
    public ApiResult<CursorPageBaseResp<ChatRoomResp>> page(CursorPageBaseReq request) {
        Long uid = UserContext.getUid();
        CursorPageBaseResp<ChatRoomResp> result = contactService.pageChatRoom(uid, request);
        return ApiResult.success(result);
    }

    @GetMapping("/public/contact/detail")
    @ApiOperation("会话详情")
    public ApiResult<ChatRoomResp> detail(@Valid IdBaseReq req) {
        Long uid = UserContext.getUid();
        ChatRoomResp chatRoomResp = contactService.getChatRoomDetail(req, uid);
        return ApiResult.success(chatRoomResp);
    }

    @GetMapping("/public/contact/detail/friend")
    @ApiOperation("会话详情(联系人列表发消息用)")
    public ApiResult<ChatRoomResp> detailFriend(@Valid ContactFriendReq req) {
        Long uid = UserContext.getUid();
        ChatRoomResp chatRoomResp = contactService.detailChatRoomByTargetId(req, uid);
        return ApiResult.success(chatRoomResp);
    }
}

