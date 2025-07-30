package com.meteor.chat.msg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageMarkActTypeEnum {
    MARK(1, "确认标记"),
    UN_MARK(2, "取消标记"),
    ;
    private Integer type;
    private String desc;
}
