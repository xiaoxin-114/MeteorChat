package com.meteor.chat.api.user;

import com.meteor.chat.api.UserInfoApi;
import com.meteor.chat.api.user.dto.UserCursorPageDTO;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserInfoCommonApi {
    /**
     * 获取用户信息
     * @param uid 用户id
     */
    ApiResult<UserInfoDTO> getUserInfo( Long uid);

    /**
     * 获取黑名单列表
     */
    ApiResult<Set<String>> getBlackList();

    /**
     * 批量获取用户信息
     * @param uidList 用户id列表
     */
    ApiResult<List<UserInfoDTO>> getUserInfoList( List<Long> uidList);

    /**
     * 批量获取用户信息
     * @param uidList 用户id列表
     */
    ApiResult<Map<Long, UserInfoDTO>> getUserInfoMap( List<Long> uidList);


    /**
     * 游标分页查询用户列表
     */
    ApiResult<CursorPageBaseResp<UserInfoDTO>> cursorPageUser(UserCursorPageDTO req);

    ApiResult<List<UserInfoDTO>> getAllUser();

    /**
     * 获取在线用户id列表
     */
    ApiResult<Set<Long>> getOnlineUidSet();
}