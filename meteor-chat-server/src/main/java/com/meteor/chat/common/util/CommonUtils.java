package com.meteor.chat.common.util;

import com.meteor.chat.common.domain.enums.IdempotenceCodeEnum;

public class CommonUtils {

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
}
