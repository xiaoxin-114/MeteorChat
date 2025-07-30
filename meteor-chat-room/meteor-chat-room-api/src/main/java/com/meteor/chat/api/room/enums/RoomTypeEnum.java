package com.meteor.chat.api.room.enums;

import lombok.Getter;

@Getter
public enum RoomTypeEnum {
    GROUP(1, "群聊"),
    SINGLE(2, "单聊");
    private Integer code;
    private String name;
    RoomTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
