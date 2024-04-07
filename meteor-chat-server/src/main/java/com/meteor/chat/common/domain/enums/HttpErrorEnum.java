package com.meteor.chat.common.domain.enums;

import cn.hutool.http.ContentType;
import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.exception.ErrorEnum;
import lombok.Getter;

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
        response.setStatus(this.errorCode);
        response.setContentType(ContentType.JSON.toString(StandardCharsets.UTF_8));
        response.getWriter().write(ApiResult.fail(this).toString());
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
