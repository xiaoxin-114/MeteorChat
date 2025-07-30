package com.meteor.chat.web.core.enums;

import lombok.Getter;

@Getter
public enum BlackTypeEnum {
    UID(1, "用户id"),
    IP(2, "用户IP"),;
    private int id;
    private String desc;
    BlackTypeEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
