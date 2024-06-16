package com.meteor.chat.common.frequency.exception;


import com.meteor.chat.common.exception.ErrorEnum;

public class FrequencyException extends RuntimeException{
    private static final long serialVersionUID = 865297300830245481L;

    private Integer code;
    private String msg;

    public FrequencyException(){
        super();
    }

    public FrequencyException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public FrequencyException(ErrorEnum errorEnum) {
        super(errorEnum.getErrMsg());
        this.code = errorEnum.getErrCode();
        this.msg = errorEnum.getErrMsg();
    }
}
