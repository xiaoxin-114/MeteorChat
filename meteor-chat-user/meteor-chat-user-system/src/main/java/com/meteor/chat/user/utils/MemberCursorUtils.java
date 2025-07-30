package com.meteor.chat.user.utils;

import cn.hutool.core.lang.Pair;
import com.meteor.chat.user.enums.ChatActiveStatusEnum;
import org.apache.commons.lang3.StringUtils;

public class MemberCursorUtils {

    private static final String SEPARATOR = "_";
    public static Pair<ChatActiveStatusEnum, String> getMemberCursor(String cursor) {
        if (StringUtils.isNotEmpty(cursor)) {
            String activeType = cursor.split(SEPARATOR)[0];
            String cursorStr = cursor.split(SEPARATOR)[1];
            ChatActiveStatusEnum activeStatusEnum = ChatActiveStatusEnum.of(Integer.parseInt(activeType));
            return Pair.of(activeStatusEnum, cursorStr);
        }
        // 如果是第一页，默认返回在线，且游标值为空
        return Pair.of(ChatActiveStatusEnum.ONLINE, null);
    }

    public static String generateMemberCursor(ChatActiveStatusEnum activeStatusEnum, String cursorStr) {
        return activeStatusEnum.getStatus() + SEPARATOR + cursorStr;
    }
}
