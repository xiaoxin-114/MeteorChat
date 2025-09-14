package com.meteor.chat.user.controller;

import com.meteor.chat.oss.core.domain.OssReq;
import com.meteor.chat.oss.core.domain.OssResp;
import com.meteor.chat.user.domain.vo.req.UploadUrlReq;
import com.meteor.chat.user.service.OssService;
import com.meteor.chat.web.core.context.UserContext;
import com.meteor.chat.common.result.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/capi/oss")
@Tag(name = "对象存储相关接口")
public class OssController {

    @Resource
    private OssService ossService;

    @GetMapping("/upload/url")
    @Operation(summary = "获取一个临时的上传链接")
    public ApiResult<OssResp> getUploadUrl(@Valid UploadUrlReq req) {
        OssResp ossResp = ossService.uploadUrl(req, UserContext.getUid());
        return ApiResult.success(ossResp);
    }
}
