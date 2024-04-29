package com.meteor.chat.common.exception;

import cn.hutool.http.HttpStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private Integer code;
    private String msg;

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    public BusinessException(String msg) {
        this(HttpStatus.HTTP_BAD_REQUEST, msg);
    }
    public BusinessException(CommonErrorEnum error) {
        this(error.getMsg());
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
