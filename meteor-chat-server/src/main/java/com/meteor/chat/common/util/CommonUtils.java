package com.meteor.chat.common.util;

import cn.hutool.core.lang.Pair;
import com.meteor.chat.common.domain.enums.ChatActiveStatusEnum;
import com.meteor.chat.common.domain.enums.IdempotenceCodeEnum;
import org.apache.commons.lang3.StringUtils;

public class CommonUtils {

    private static final String SEPARATOR = "_";

    /**
     * 发放物品时对幂等号进行拼接
     * @param itemId 物品id
     * @param codeEnum 幂等号类型
     * @param bussinessId 幂等号标识
     * @return 幂等号
     */
    public static String getIdempotent(Long itemId, IdempotenceCodeEnum codeEnum, String bussinessId) {
        return String.format("%s_%s_%s", itemId, codeEnum.getDescr(), bussinessId);
    }

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
