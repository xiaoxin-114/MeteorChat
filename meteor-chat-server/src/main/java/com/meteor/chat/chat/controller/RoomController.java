package com.meteor.chat.chat.controller;

import com.meteor.chat.chat.service.RoomService;
import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.GroupMemberListResp;
import com.meteor.chat.common.domain.vo.GroupMemberResp;
import com.meteor.chat.common.domain.vo.GroupResp;
import com.meteor.chat.common.domain.vo.req.IdBaseReq;
import com.meteor.chat.common.domain.vo.req.MemberCursorReq;
import com.meteor.chat.common.domain.vo.req.MemberDelReq;
import com.meteor.chat.common.util.UserContext;
import io.github.classgraph.json.Id;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ApiResult<List<GroupMemberListResp>> getMemberList(@Valid IdBaseReq req) {
        List<GroupMemberListResp> result = roomService.getMemberList(req);
        return ApiResult.success(result);
    }

    @DeleteMapping("/group/member")
    @ApiOperation("移除成员")
    public ApiResult removeMember(@Valid MemberDelReq req) {
        Long uid = UserContext.getUid();
        roomService.removeMember(req, uid);
        return ApiResult.success();
    }

    @DeleteMapping("/group/member/exit")
    @ApiOperation("退出群聊")
    public ApiResult exitChatGroup(@Valid IdBaseReq req) {
        Long uid = UserContext.getUid();
        roomService.exitRoom(req, uid);
        return ApiResult.success();
    }

}
