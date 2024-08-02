package com.meteor.chat.msg.controller;

import com.meteor.chat.common.domain.dto.MsgReadInfoDTO;
import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.domain.vo.ChatMessageReadResp;
import com.meteor.chat.common.domain.vo.ChatMessageResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.*;
import com.meteor.chat.common.frequency.annotation.FrequencyControl;
import com.meteor.chat.common.util.UserContext;
import com.meteor.chat.msg.service.MessageService;
import com.meteor.chat.oss.MinIOTemplate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@ApiModel("消息模块")
@RequestMapping("/capi/chat")
@RestController
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

    @FrequencyControl(time = 5, count = 10, type = FrequencyControl.FrequencyTypeEnum.UID)
    @FrequencyControl(time = 10, count = 15, type = FrequencyControl.FrequencyTypeEnum.UID)
    @PostMapping("/msg")
    @ApiOperation("发送消息")
    public ApiResult<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq request) {
        Long uid = UserContext.getUid();
        Long msgId = messageService.sendMsg(request, uid);
        //返回完整消息格式，方便前端展示
        ChatMessageResp chatMessageResp = messageService.getMessageResp(msgId, uid);
        return ApiResult.success(chatMessageResp);
    }

    @GetMapping("/public/msg/page")
    @ApiOperation("消息列表")
    public ApiResult<CursorPageBaseResp<ChatMessageResp>> getMsgPage(@Valid MessageCursorReq req) {
        Long uid = UserContext.getUid();
        CursorPageBaseResp<ChatMessageResp> chatMessageRespCursorPage = messageService.cursorChatMessageResp(req, uid);
        return ApiResult.success(chatMessageRespCursorPage);
    }

    @PutMapping("/msg/recall")
    @ApiOperation("撤回消息")
    public ApiResult<Void> recallMsg(@Valid @RequestBody MsgRecallReq req) {
        messageService.recall(req, UserContext.getUid());
        return ApiResult.success();
    }

    @PutMapping("/msg/read")
    @ApiOperation("消息阅读上报")
    public ApiResult<Void> readMsg(@Valid @RequestBody ChatMessageMemberReq req) {
        messageService.readMsg(req, UserContext.getUid());
        return ApiResult.success();
    }
}
