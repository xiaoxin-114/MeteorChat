package com.meteor.chat.room.controller;

import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.room.domain.vo.*;
import com.meteor.chat.room.service.RoomService;
import com.meteor.chat.web.core.context.UserContext;
import com.meteor.chat.common.result.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

@RequestMapping("/capi/room")
@RestController
@Slf4j
@Tag(name = "群聊管理")
public class RoomController {

    @Resource
    private RoomService roomService;

    @GetMapping("/public/group")
    @Operation(summary = "群组详情")
    public ApiResult<GroupResp> groupDetail(@Valid IdBaseReq req) {
        Long uid = UserContext.getUid();
        GroupResp memberResp = roomService.groupDetail(req, uid);
        return ApiResult.success(memberResp);
    }

    @GetMapping("/public/group/member/page")
    @Operation(summary = "群成员列表")
    public ApiResult<CursorPageBaseResp<GroupMemberResp>> memberPage(@Valid RoomMemberCursorReq req) {
        CursorPageBaseResp<GroupMemberResp> cursorPage = roomService.cursorPageMember(req);
        return ApiResult.success(cursorPage);
    }

    @GetMapping("/group/member/list")
    @Operation(description = "房间内的所有群成员列表-@专用")
    // todo 当前只是前端存储来进行搜索，而且后端只返回了1000条数据，后续需要优化，否则会出现搜索结果不全的情况
    public ApiResult<List<GroupMemberListResp>> getMemberList(@Valid RoomMemberReq req) {
        List<GroupMemberListResp> result = roomService.getMemberList(req);
        return ApiResult.success(result);
    }

    @DeleteMapping("/group/member")
    @Operation(summary = "移除成员")
    public ApiResult<Void> delMember(@Valid @RequestBody MemberDelReq req) {
        Long uid = UserContext.getUid();
        roomService.removeMember(req, uid);
        return ApiResult.success();
    }

    @DeleteMapping("/group/member/exit")
    @Operation(summary = "退出群聊")
    public ApiResult<Void> exitRoom(@Valid @RequestBody MemberExitReq req) {
        Long uid = UserContext.getUid();
        roomService.exitRoom(req, uid);
        return ApiResult.success();
    }

    @PostMapping("/group")
    @Operation(summary = "新增群组")
    public ApiResult<IdRespVO> createChatGroup(@RequestBody @Valid GroupAddReq req) {
        Long uid = UserContext.getUid();
        Long roomId = roomService.createChatGroup(req, uid);
        return ApiResult.success(IdRespVO.id(roomId));
    }

    @PostMapping("/group/member")
    @Operation(summary = "邀请好友")
    public ApiResult<Void> addGroupMembers(@Valid @RequestBody MemberAddReq req) {
        Long uid = UserContext.getUid();
        roomService.addGroupMembers(req, uid);
        return ApiResult.success();
    }

    @PutMapping("/group/admin")
    @Operation(summary = "添加管理员")
    public ApiResult<Void> addAdmin(@Valid @RequestBody AdminChangeReq req) {
        Long uid = UserContext.getUid();
        roomService.addAdmin(req, uid);
        return ApiResult.success();
    }

    @DeleteMapping("/group/admin")
    @Operation(summary = "移除管理员")
    public ApiResult<Void> removeAdmin(@Valid @RequestBody AdminChangeReq req) {
        Long uid = UserContext.getUid();
        roomService.removeAdmin(req, uid);
        return ApiResult.success();
    }
}
