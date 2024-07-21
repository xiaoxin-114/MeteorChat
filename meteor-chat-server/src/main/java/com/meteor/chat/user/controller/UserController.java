package com.meteor.chat.user.controller;

import com.meteor.chat.common.domain.dto.ItemInfoDTO;
import com.meteor.chat.common.domain.dto.SummaryInfoDTO;
import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.domain.vo.BadgeResp;
import com.meteor.chat.common.domain.vo.UserInfoVO;
import com.meteor.chat.common.domain.vo.req.*;
import com.meteor.chat.common.exception.CommonErrorEnum;
import com.meteor.chat.common.exception.ErrorEnum;
import com.meteor.chat.common.util.UserContext;
import com.meteor.chat.user.service.UserBackpackService;
import com.meteor.chat.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/capi/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private UserBackpackService userBackpackService;

    @GetMapping("/userInfo")
    public ApiResult<UserInfoVO> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(UserContext.get().getUid()));
    }

    @PutMapping("/black")
    public ApiResult black(@RequestBody @Valid BlackReq req) {
        Long uid = UserContext.get().getUid();
        if (!userService.isAdmin(uid)) {
            return ApiResult.fail(CommonErrorEnum.NOT_PERMITTED);
        }
        userService.black(req.getBlackId());
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @ApiOperation("获取徽章图鉴")
    public ApiResult<List<BadgeResp>> getBadgeList() {
        Long uid = UserContext.getUid();
        return ApiResult.success(userBackpackService.allBadgeList(uid));
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult wearBadge(@RequestBody WearBadgeReq req) {
        Long uid = UserContext.getUid();
        userService.wearBadge(uid, req.getBadgeId());
        return ApiResult.success();
    }

    @PutMapping("/name")
    @ApiOperation("用户修改名称")
    public ApiResult rename(@RequestBody @Valid ModifyNameReq req) {
        Long uid = UserContext.getUid();
        userService.rename(uid, req);
        return ApiResult.success();
    }

    @PostMapping("/public/summary/userInfo/batch")
    @ApiOperation("批量懒加载更新用户数据")
    public ApiResult<List<SummaryInfoDTO>> batchRefreshUserInfo(@RequestBody @Valid SummaryInfoReq req) {
        return ApiResult.success(userService.getSummaryInfoDTOList(req));
    }

    @PostMapping("/public/badges/batch")
    @ApiOperation("批量懒加载徽章数据")
    public ApiResult<List<ItemInfoDTO>> batchRefreshBagdesInfo(@RequestBody @Valid ItemInfoReq req) {
        return ApiResult.success(userService.getItemInfoDTOList(req));
    }

}
