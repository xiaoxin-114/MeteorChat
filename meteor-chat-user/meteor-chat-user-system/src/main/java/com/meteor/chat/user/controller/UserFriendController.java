package com.meteor.chat.user.controller;

import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.mybatis.domain.CursorPageBaseReq;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.user.domain.vo.*;
import com.meteor.chat.user.domain.vo.req.*;
import com.meteor.chat.user.service.UserFriendService;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.web.core.context.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import jakarta.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/capi/user/friend")
public class UserFriendController {

    @Resource
    private UserFriendService friendService;

    @GetMapping("/page")
    @Operation(summary = "获取好友列表分页数据")
    public ApiResult<CursorPageBaseResp<FriendResp>> page(@Valid CursorPageBaseReq request) {
        Long uid = UserContext.getUid();
        CursorPageBaseResp<FriendResp> result = friendService.pageFriendList(uid, request);
        return ApiResult.success(result);
    }

    @GetMapping("/check")
    @Operation(summary = "批量检查是否是好友")
    public ApiResult<FriendCheckResp> batchCheckFriend(@Valid FriendCheckReq req) {
        Long uid = UserContext.getUid();
        FriendCheckResp friendCheckResp = friendService.batchCheckFriends(uid, req);
        return ApiResult.success(friendCheckResp);
    }

    @PostMapping("/apply")
    @Operation(summary = "发送好友申请")
    public ApiResult<Void> apply(@RequestBody @Valid UserApplyReq req) {
        Long uid = UserContext.getUid();
        friendService.applyUser(uid, req.getTargetUid(), req.getMsg());
        return ApiResult.success();
    }

    @GetMapping("/apply/unread")
    @Operation(summary = "获取未读好友申请数量")
    public ApiResult<FriendUnreadResp> getUnreadCount() {
        Long uid = UserContext.getUid();
        return ApiResult.success(friendService.countUnread(uid));
    }

    @GetMapping("/apply/page")
    @Operation(summary = "获取好友申请分页数据")
    public ApiResult<PageBaseResp<FriendApplyResp>> applyPage(@Valid PageBaseReq req) {
        Long uid = UserContext.getUid();
        PageBaseResp<FriendApplyResp> friendApplyPage = friendService.applyPage(req, uid);
        return ApiResult.success(friendApplyPage);
    }

    @PutMapping("/apply")
    @Operation(summary = "同意好友申请")
    public ApiResult<Void> processApply(@Valid @RequestBody FriendApproveReq req) {
        friendService.processApply(req);
        return ApiResult.success();
    }

    @DeleteMapping("")
    @Operation(summary = "删除好友")
    public ApiResult<Void> deleteFriend(@RequestBody @Valid FriendDeleteReq req) {
        Long uid = UserContext.getUid();
        friendService.deleteFriend(req, uid);
        return ApiResult.success();
    }
}
