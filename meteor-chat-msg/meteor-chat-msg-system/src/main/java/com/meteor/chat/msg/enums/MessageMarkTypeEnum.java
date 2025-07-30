package com.meteor.chat.msg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageMarkTypeEnum {
    LIKE(1, "点赞"),
    UNLIKE(2, "点踩");

    private Integer code;
    private String desc;
}
