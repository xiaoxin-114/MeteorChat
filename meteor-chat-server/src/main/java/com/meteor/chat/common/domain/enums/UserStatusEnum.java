package com.meteor.chat.common.domain.enums;

import lombok.Getter;

@Getter
public enum UserStatusEnum {
    NORMAL(0, "正常"),
    BLACK(1, "被拉黑"),;
    private int id;
    private String desc;
    UserStatusEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
