package com.meteor.chat.chat.controller;

import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.domain.vo.ChatRoomResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.CursorPageBaseReq;
import com.meteor.chat.common.util.UserContext;
import com.meteor.chat.chat.service.RoomService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/capi/chat/public/contact")
@ApiOperation("会话请求")
public class ContactController {

    @Resource
    private RoomService roomService;

    @GetMapping("/page")
    public ApiResult<CursorPageBaseResp<ChatRoomResp>> page(CursorPageBaseReq request) {
        Long uid = UserContext.getUid();
        CursorPageBaseResp<ChatRoomResp> result = roomService.pageChatRoom(uid, request);
        return ApiResult.success(result);
    }
}
