package com.meteor.chat.common.domain.enums;

import lombok.Getter;

@Getter
public enum RoomFriendStatusEnum {
    NORAML(0, "正常"),
    FORBID(1, "禁用");

    private int code;
    private String desc;
    RoomFriendStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
