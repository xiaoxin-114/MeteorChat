package com.meteor.chat.room.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum GroupRoleAPPEnum {
    LEADER(1, "群主"),
    MANAGER(2, "管理"),
    MEMBER(3, "普通成员"),
    REMOVE(4, "被移除的成员"),
    ;
    private Integer code;

    private String desc;

    private static Map<Integer, GroupRoleAPPEnum> cache = new HashMap<>();

    static {
        Arrays.stream(GroupRoleAPPEnum.values()).forEach(role -> cache.put(role.getCode(), role));
    }

    public static GroupRoleAPPEnum of(Integer code) {
        return cache.get(code);
    }
}
