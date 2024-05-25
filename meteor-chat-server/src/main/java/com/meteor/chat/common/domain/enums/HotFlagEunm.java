package com.meteor.chat.common.domain.enums;

import lombok.Getter;

@Getter
public enum HotFlagEunm {
    NORAML(0, "非热点群聊"),
    HOT_ROOM(1, "热点群聊");

    private int code;
    private String desc;
    HotFlagEunm(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
