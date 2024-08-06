package com.meteor.chat.user.controller;

import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.domain.vo.IdRespVO;
import com.meteor.chat.common.domain.vo.UserEmojiResp;
import com.meteor.chat.common.domain.vo.req.IdBaseReq;
import com.meteor.chat.common.domain.vo.req.UserEmojiAddReq;
import com.meteor.chat.common.util.UserContext;
import com.meteor.chat.user.service.UserEmojiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/capi/user/emoji")
@Api(tags = "用户表情包管理相关接口")
public class UserEmojiController {

    @Resource
    private UserEmojiService userEmojiService;

    @GetMapping("/list")
    @ApiOperation("获取用户表情包列表")
    public ApiResult<List<UserEmojiResp>> listEmoji() {
        Long uid = UserContext.getUid();
        List<UserEmojiResp> emojiList = userEmojiService.listEmoji(uid);
        return ApiResult.success(emojiList);
    }

    @PostMapping()
    @ApiOperation("新增表情包")
    public ApiResult<IdRespVO> addEmoji(@RequestBody @Valid UserEmojiAddReq req) {
        Long uid = UserContext.getUid();
        IdRespVO idRespVO = userEmojiService.addEmoji(req, uid);
        return ApiResult.success(idRespVO);
    }

    @DeleteMapping()
    @ApiOperation("删除表情包")
    public ApiResult<Void> removeEmoji(@RequestBody @Valid IdBaseReq req){
        Long uid = UserContext.getUid();
        userEmojiService.removeEmoji(req, uid);
        return ApiResult.success();
    }


}
