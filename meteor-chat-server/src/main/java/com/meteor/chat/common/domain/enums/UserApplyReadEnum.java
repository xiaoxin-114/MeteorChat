package com.meteor.chat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum UserApplyReadEnum {
    UNREAD(1, "未读"),
    READED(2, "已读");
    private Integer code;
    private String desc;
}
