package com.meteor.chat.user.api;

import com.meteor.chat.api.user.UserInfoCommonApi;
import com.meteor.chat.api.user.dto.UserCursorPageDTO;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.domain.entity.User;
import com.meteor.chat.user.domain.vo.req.MemberCursorReq;
import com.meteor.chat.user.service.UserService;
import com.meteor.chat.user.service.cache.UserCache;
import com.meteor.chat.user.service.cache.UserInfoCache;
import com.meteor.chat.api.UserInfoApi;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@RestController
public class UserInfoCommonApiImpl implements UserInfoCommonApi, UserInfoApi {

    @Resource
    private UserCache userCache;
    @Resource
    private UserInfoCache userInfoCache;
    @Resource
    private UserService userService;
    @Resource
    private UserDao userDao;

    @Override
    public ApiResult<Map<Integer, Set<String>>> getBlackMap() {
        return ApiResult.success(userCache.getBlackMap());
    }

    @Override
    public ApiResult<Set<String>> getBlackList() {
        return ApiResult.success(userCache.getBlackList());
    }

    @Override
    public ApiResult<UserInfoDTO> getUserInfo(Long uid) {
        User userInfo = userCache.getUserInfo(uid);
        return ApiResult.success(UserInfoDTO.builder().uid(uid)
                .name(userInfo.getName())
                .avatar(userInfo.getAvatar()).build());
    }

    @Override
    public ApiResult<List<UserInfoDTO>> getUserInfoList(List<Long> uidList) {
        Collection<User> list = userInfoCache.getList(uidList);
        if (list != null && !list.isEmpty()) {
            return ApiResult.success(list.stream().map(user -> UserInfoDTO.builder()
                    .uid(user.getId())
                    .lastOptTime(user.getLastOptTime())
                    .activeStatus(user.getActiveStatus())
                    .name(user.getName())
                    .avatar(user.getAvatar()).build()).collect(Collectors.toList()));
        }
        return ApiResult.success(new ArrayList<>());
    }

    @Override
    public ApiResult<Map<Long, UserInfoDTO>> getUserInfoMap(List<Long> uidList) {
        Collection<User> list = userInfoCache.getList(uidList);
        if (list != null && !list.isEmpty()) {
            return ApiResult.success(list.stream().map(user -> UserInfoDTO.builder()
                    .uid(user.getId())
                    .lastOptTime(user.getLastOptTime())
                    .activeStatus(user.getActiveStatus())
                    .name(user.getName())
                    .avatar(user.getAvatar()).build()).collect(Collectors.toMap(UserInfoDTO::getUid, Function.identity())));
        }
        return ApiResult.success(new HashMap<>());
    }

    @Override
    public ApiResult<CursorPageBaseResp<UserInfoDTO>> cursorPageUser(UserCursorPageDTO req) {
        MemberCursorReq memberCursorReq = new MemberCursorReq();
        memberCursorReq.setRoomId(req.getRoomId());
        memberCursorReq.setPageSize(req.getPageSize());
        memberCursorReq.setCursor(req.getCursor());
        CursorPageBaseResp<User> userCursorPage = userService.cursorPageUser(memberCursorReq, req.getUidList());
        List<UserInfoDTO> data = userCursorPage.getList().stream().map(user -> UserInfoDTO.builder().uid(user.getId())
                .avatar(user.getAvatar())
                .name(user.getName())
                .activeStatus(user.getActiveStatus())
                .lastOptTime(user.getLastOptTime())
                .build()).collect(Collectors.toList());
        return ApiResult.success(CursorPageBaseResp.init(userCursorPage, data));
    }


    @Override
    public ApiResult<List<UserInfoDTO>> getAllUser() {
        List<User> memberList = userDao.getMemberList();
        if (memberList.isEmpty()) {
            return ApiResult.success(Collections.emptyList());
        }
        return ApiResult.success(memberList.stream().map(user -> UserInfoDTO.builder()
                .uid(user.getId())
                .avatar(user.getAvatar())
                .name(user.getName())
                .activeStatus(user.getActiveStatus())
                .lastOptTime(user.getLastOptTime())
                .build()).collect(Collectors.toList()));
    }

    @Override
    public ApiResult<Set<Long>> getOnlineUidSet() {
        Set<String> uidStrSet = userCache.getOnlineUidList();
        if (uidStrSet == null) {
            return ApiResult.success(Collections.emptySet());
        }
        return ApiResult.success(uidStrSet.stream().map(Long::parseLong).collect(Collectors.toSet()));
    }
}
