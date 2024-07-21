package com.meteor.chat.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum YesOrNoEnum {
    YES(1, "是"),
    NO(0, "否");
    private int code;
    private String desc;

    private static Map<Integer, YesOrNoEnum> map;

    static {
        map = Arrays.stream(YesOrNoEnum.values()).collect(Collectors.toMap(YesOrNoEnum::getCode, Function.identity()));
    }

    public static YesOrNoEnum get(Integer code) {
        return map.get(code);
    }

    public static Integer toStatus(Boolean bool) {
        return bool ? YES.getCode() : NO.getCode();
    }
}
