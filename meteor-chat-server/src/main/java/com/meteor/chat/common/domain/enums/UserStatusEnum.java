package com.meteor.chat.common.domain.enums;

import lombok.Getter;

@Getter
public enum UserStatusEnum {
    NORMAL(0, "正常"),
    BLACK(1, "被拉黑"),
    INNER(2, "内置用户");
    private Integer id;
    private String desc;
    UserStatusEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
