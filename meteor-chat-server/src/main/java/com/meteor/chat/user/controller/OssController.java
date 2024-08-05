package com.meteor.chat.user.controller;

import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.domain.vo.req.UploadUrlReq;
import com.meteor.chat.common.util.UserContext;
import com.meteor.chat.oss.domain.OssResp;
import com.meteor.chat.user.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/capi/oss")
@Api("对象存储相关接口")
public class OssController {

    @Resource
    private OssService ossService;

    @GetMapping("/upload/url")
    @ApiOperation("获取一个临时的上传链接")
    public ApiResult<OssResp> getUploadUrl(@Valid UploadUrlReq req) {
        OssResp ossResp = ossService.uploadUrl(req, UserContext.getUid());
        return ApiResult.success(ossResp);
    }
}
