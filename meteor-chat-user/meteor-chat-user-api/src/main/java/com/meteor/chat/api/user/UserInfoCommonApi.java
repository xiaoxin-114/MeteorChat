package com.meteor.chat.api.user;

import com.meteor.chat.api.user.constants.ApiConstants;
import com.meteor.chat.api.user.dto.UserCursorPageDTO;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.common.constants.RpcConstants;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;
@FeignClient(name = RpcConstants.USER_SERVER_NAME)
public interface UserInfoCommonApi {
    /**
     * 获取用户信息
     * @param uid 用户id
     */
    @GetMapping(ApiConstants.USER_PREFIX + "/info")
    UserInfoDTO getUserInfo(Long uid);

    /**
     * 获取黑名单列表
     */
    @GetMapping(ApiConstants.USER_PREFIX + "/blacklist")
    Set<String> getBlackList();

    /**
     * 批量获取用户信息
     * @param uidList 用户id列表
     */
    @PostMapping(ApiConstants.USER_PREFIX + "/info/list")
    List<UserInfoDTO> getUserInfoList(@RequestBody List<Long> uidList);

    /**
     * 批量获取用户信息
     * @param uidList 用户id列表
     */
    @PostMapping(ApiConstants.USER_PREFIX + "/info/map")
    Map<Long, UserInfoDTO> getUserInfoMap(@RequestBody List<Long> uidList);


    /**
     * 游标分页查询用户列表
     */
    @PostMapping(ApiConstants.USER_PREFIX + "/info/page")
    CursorPageBaseResp<UserInfoDTO> cursorPageUser(@RequestBody UserCursorPageDTO req);

    @GetMapping(ApiConstants.USER_PREFIX + "/info/all")
    List<UserInfoDTO> getAllUser();

    /**
     * 获取在线用户id列表
     */
    @GetMapping(ApiConstants.USER_PREFIX + "/online")
    Set<Long> getOnlineUidSet();
}
