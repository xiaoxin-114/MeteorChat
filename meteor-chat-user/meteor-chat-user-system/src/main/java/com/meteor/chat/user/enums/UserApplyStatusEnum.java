package com.meteor.chat.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum UserApplyStatusEnum {
    WAITING(1, "待审批"),
    PERMITTED(2, "已同意");
    private Integer code;
    private String desc;
}
