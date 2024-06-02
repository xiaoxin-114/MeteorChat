package com.meteor.chat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum YesOrNoEnum {
    YES(1, "是"),
    NO(0, "否");
    private int code;
    private String desc;
}
