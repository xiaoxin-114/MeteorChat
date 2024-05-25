package com.meteor.chat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum UserApplyTypeEnum {
    GETFRIEND(1, "加好友");
    private Integer code;
    private String desc;
}
