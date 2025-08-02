package com.meteor.chat.web.core.enums;

import cn.hutool.http.ContentType;
import com.meteor.chat.common.exception.ErrorEnum;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.web.core.utils.WebFrameworkUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public enum HttpErrorEnum implements ErrorEnum {
    ACCESS_DENIED(401, "登录失败，请重新登录");

    private Integer errorCode;
    private String errorMsg;

    HttpErrorEnum(int code, String msg){
        this.errorCode = code;
        this.errorMsg = msg;
    }

    public void sendErrorResponse(HttpServletResponse response) throws IOException {
        WebFrameworkUtils.sendErrorMsg(response, ApiResult.fail(this));
    }

    @Override
    public Integer getErrCode() {
        return this.errorCode;
    }

    @Override
    public String getErrMsg() {
        return this.errorMsg;
    }
}
