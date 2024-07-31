package com.meteor.chat.chat.controller;

import com.meteor.chat.chat.service.RoomService;
import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.domain.vo.*;
import com.meteor.chat.common.domain.vo.req.*;
import com.meteor.chat.common.util.UserContext;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("/capi/room")
@RestController
@Slf4j
@ApiModel("群聊管理")
public class RoomController {

    @Resource
    private RoomService roomService;

    @GetMapping("/public/group")
    @ApiOperation("群组详情")
    public ApiResult<GroupResp> groupDetail(@Valid IdBaseReq req) {
        Long uid = UserContext.getUid();
        GroupResp memberResp = roomService.groupDetail(req, uid);
        return ApiResult.success(memberResp);
    }

    @GetMapping("/public/group/member/page")
    @ApiOperation("群成员列表")
    public ApiResult<CursorPageBaseResp<GroupMemberResp>> memberPage(@Valid MemberCursorReq req) {
        CursorPageBaseResp<GroupMemberResp> cursorPage = roomService.cursorPageMember(req);
        return ApiResult.success(cursorPage);
    }

    @GetMapping("/group/member/list")
    @ApiOperation("房间内的所有群成员列表-@专用")
    public ApiResult<List<GroupMemberListResp>> getMemberList(@Valid ChatMessageMemberReq req) {
        List<GroupMemberListResp> result = roomService.getMemberList(req);
        return ApiResult.success(result);
    }

    @DeleteMapping("/group/member")
    @ApiOperation("移除成员")
    public ApiResult<Void> removeMember(@Valid @RequestBody MemberDelReq req) {
        Long uid = UserContext.getUid();
        roomService.removeMember(req, uid);
        return ApiResult.success();
    }

    @DeleteMapping("/group/member/exit")
    @ApiOperation("退出群聊")
    public ApiResult<Void> exitChatGroup(@Valid @RequestBody MemberExitReq req) {
        Long uid = UserContext.getUid();
        roomService.exitRoom(req, uid);
        return ApiResult.success();
    }

    @PostMapping("/group")
    @ApiOperation("新增群组")
    public ApiResult<IdRespVO> createChatGroup(@RequestBody @Valid GroupAddReq req) {
        Long uid = UserContext.getUid();
        Long roomId = roomService.createChatGroup(req, uid);
        return ApiResult.success(IdRespVO.id(roomId));
    }

    @PostMapping("/group/member")
    @ApiOperation("邀请好友")
    public ApiResult<Void> addGroupMembers(@RequestBody @Valid MemberAddReq req) {
        Long uid = UserContext.getUid();
        roomService.addGroupMembers(req, uid);
        return ApiResult.success();
    }

    @PutMapping("/group/admin")
    @ApiOperation("添加管理员")
    public ApiResult<Void> addAdmin(@RequestBody @Valid AdminChangeReq req) {
        Long uid = UserContext.getUid();
        roomService.addAdmin(req, uid);
        return ApiResult.success();
    }

    @DeleteMapping("/group/admin")
    @ApiOperation("移除管理员")
    public ApiResult<Void> removeAdmin(@RequestBody @Valid AdminChangeReq req) {
        Long uid = UserContext.getUid();
        roomService.removeAdmin(req, uid);
        return ApiResult.success();
    }
}
