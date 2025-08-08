package com.meteor.chat.user.api;

import com.meteor.chat.api.UserInfoApi;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.user.service.cache.UserCache;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@DubboService
public class UserInfoApiImpl implements UserInfoApi {

    @Resource
    private UserCache userCache;

    @Override
    public ApiResult<Map<Integer, Set<String>>> getBlackMap() {
        return ApiResult.success(userCache.getBlackMap());
    }
}
