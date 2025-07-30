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
    NOT_PERMITTED(-5, "没有权限访问"),
    PARAM_ERROR(-6, "数据异常"),
    USER_NOT_EXIST(-7, "用户不存在"),
    USERNAME_OR_PASSWORD_ERROR(-8, "用户名或密码错误"),
    USERNAME_OR_PASSWORD_EMPTY(-9, "用户名或密码不能为空"),
    USER_IN_BLACK(-10, "登陆用户已经被拉黑"),
    INNER_USER_LOGIN(-11, "内置用户不能登录")
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
