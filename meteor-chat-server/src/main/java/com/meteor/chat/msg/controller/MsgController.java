package com.meteor.chat.msg.controller;

import com.meteor.chat.common.domain.dto.MsgReadInfoDTO;
import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.domain.vo.ChatMessageReadResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.MessageReadCursorPageReq;
import com.meteor.chat.common.domain.vo.req.MessageReadInfoReq;
import com.meteor.chat.common.util.UserContext;
import com.meteor.chat.msg.service.MessageService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@ApiModel("消息模块")
@RequestMapping("/capi/chat")
public class MsgController {

    @Resource
    private MessageService messageService;

    @GetMapping("/msg/read/page")
    @ApiOperation("消息的已读未读列表")
    public ApiResult<CursorPageBaseResp<ChatMessageReadResp>> cursorPageMsgReader(@Valid MessageReadCursorPageReq req) {
        CursorPageBaseResp<ChatMessageReadResp> result = messageService.cursorPageMsgReader(req);
        return ApiResult.success(result);
    }

    @GetMapping("/msg/read")
    @ApiOperation("获取消息的已读未读总数")
    public ApiResult<List<MsgReadInfoDTO>> countReadAndUnRead(@Valid MessageReadInfoReq req) {
        Long uid = UserContext.getUid();
        List<MsgReadInfoDTO> result = messageService.countReadAndUnRead(req, uid);
        return ApiResult.success(result);
    }

}
