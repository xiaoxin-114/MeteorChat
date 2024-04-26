package com.meteor.chat.common.domain.enums;

import lombok.Getter;

@Getter
public enum IdempotenceCodeEnum {
    UID(1, "uid"), MSGID(2, "msg_id");

    private final Integer type;
    private final String descr;
    private IdempotenceCodeEnum(int type, String descr){
        this.type = type;
        this.descr = descr;
    }
}
