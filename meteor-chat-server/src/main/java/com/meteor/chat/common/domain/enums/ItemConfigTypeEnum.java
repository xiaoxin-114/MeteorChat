package com.meteor.chat.common.domain.enums;

import lombok.Getter;

@Getter
public enum ItemConfigTypeEnum {
    MODIFY_NAME_CARD(1, "改名卡"),
    BADGE(2, "徽章"),
    ;
    private Integer type;
    private String desc;
    ItemConfigTypeEnum (int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
