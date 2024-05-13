package com.meteor.chat.common.domain.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    SUPERADMIN(1, "抹茶管理员"),
    CHAT_ADMIN(2, "群聊管理员"),
    NORMAL(3, "普通用户");
    private Long id;
    private String name;
    RoleEnum(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
