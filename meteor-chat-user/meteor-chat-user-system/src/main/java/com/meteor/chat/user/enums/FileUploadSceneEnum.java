package com.meteor.chat.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum FileUploadSceneEnum {
    CHAT(1,"/chat","聊天"),
    EMOJI( 2,"/emoji", "表情包");

    private Integer type;

    private String path;

    private String desc;

    private static final Map<Integer, FileUploadSceneEnum> cacheMap;

    static {
        cacheMap = new HashMap<>();
        for (FileUploadSceneEnum value : FileUploadSceneEnum.values()) {
            cacheMap.put(value.type, value);
        }
    }

    public static FileUploadSceneEnum of(Integer type) {
        return cacheMap.get(type);
    }
}
