package com.meteor.chat.common.domain.enums;

import lombok.Getter;

@Getter
public enum DeleteStatusEunm {
    NORAML(0, "正常"),
    DELETE(1, "删除");

    private int code;
    private String desc;
    DeleteStatusEunm(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
