package com.meteor.chat.api;

import com.meteor.chat.common.constants.RpcConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
import java.util.Set;
@FeignClient(name = RpcConstants.USER_SERVER_NAME)
public interface UserInfoApi {
    /**
     * 获取黑名单
     * @return key为拉黑类型，分为ip和uid，value为拉黑列表
     */
    @GetMapping("/api/user/blackMap")
    Map<Integer, Set<String>> getBlackMap();
}
