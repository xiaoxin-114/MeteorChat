package com.meteor.chat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ReadEnum {
    UNREAD(2, "未读"),
    READED(1, "已读");
    private Integer code;
    private String desc;
}
