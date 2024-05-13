package com.meteor.chat.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum {

    SYSTEM_ERROR(-1, "系统出小差了，请稍后再试哦~~"),
    PARAM_VALID(-2, "参数校验失败{0}"),
    FREQUENCY_LIMIT(-3, "请求太频繁了，请稍后再试哦~~"),
    LOCK_LIMIT(-4, "请求太频繁了，请稍后再试哦~~"),
    NOT_PERMITTED(-5, "没有权限访问")
    ;
    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrCode() {
        return this.code;
    }

    @Override
    public String getErrMsg() {
        return this.msg;
    }
}
