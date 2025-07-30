package com.meteor.chat.user.controller;

import com.meteor.chat.common.domain.CursorPageBaseReq;
import com.meteor.chat.common.domain.CursorPageBaseResp;
import com.meteor.chat.user.domain.vo.*;
import com.meteor.chat.user.domain.vo.req.*;
import com.meteor.chat.user.service.UserFriendService;
import com.meteor.chat.web.core.context.UserContext;
import com.meteor.chat.common.result.ApiResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/capi/user/friend")
public class UserFriendController {

    @Resource
    private UserFriendService friendService;

    @GetMapping("/page")
    public ApiResult<CursorPageBaseResp<FriendResp>> page(@Valid CursorPageBaseReq request) {
        Long uid = UserContext.getUid();
        CursorPageBaseResp<FriendResp> result = friendService.pageFriendList(uid, request);
        return ApiResult.success(result);
    }

    @GetMapping("/check")
    public ApiResult<FriendCheckResp> batchCheckFriend(@Valid FriendCheckReq req) {
        Long uid = UserContext.getUid();
        FriendCheckResp friendCheckResp = friendService.batchCheckFriends(uid, req);
        return ApiResult.success(friendCheckResp);
    }

    @PostMapping("/apply")
    public ApiResult<Void> apply(@RequestBody @Valid UserApplyReq req) {
        Long uid = UserContext.getUid();
        friendService.applyUser(uid, req.getTargetUid(), req.getMsg());
        return ApiResult.success();
    }

    @GetMapping("/apply/unread")
    public ApiResult<FriendUnreadResp> getUnreadCount() {
        Long uid = UserContext.getUid();
        return ApiResult.success(friendService.countUnread(uid));
    }

    @GetMapping("/apply/page")
    public ApiResult<PageBaseResp<FriendApplyResp>> applyPage(@Valid PageBaseReq req) {
        Long uid = UserContext.getUid();
        PageBaseResp<FriendApplyResp> friendApplyPage = friendService.applyPage(req, uid);
        return ApiResult.success(friendApplyPage);
    }

    @PutMapping("/apply")
    @ApiOperation("同意申请")
    public ApiResult<Void> processApply(@Valid @RequestBody FriendApproveReq req) {
        friendService.processApply(req);
        return ApiResult.success();
    }

    @DeleteMapping("")
    @ApiOperation("删除好友")
    public ApiResult<Void> deleteFriend(@RequestBody @Valid FriendDeleteReq req) {
        Long uid = UserContext.getUid();
        friendService.deleteFriend(req, uid);
        return ApiResult.success();
    }
}
