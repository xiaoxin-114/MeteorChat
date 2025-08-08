package com.meteor.chat.api;

import com.meteor.chat.common.result.ApiResult;

import java.util.Map;
import java.util.Set;

public interface UserInfoApi {
    /**
     * 获取黑名单
     * @return key为拉黑类型，分为ip和uid，value为拉黑列表
     */
    ApiResult<Map<Integer, Set<String>>> getBlackMap();
}