package com.meteor.chat.user.api;

import com.meteor.chat.api.user.UserInfoCommonApi;
import com.meteor.chat.api.user.dto.UserCursorPageDTO;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.common.domain.CursorPageBaseResp;
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
    public Map<Integer, Set<String>> getBlackMap() {
        return userCache.getBlackMap();
    }

    @Override
    public Set<String> getBlackList() {
        return userCache.getBlackList();
    }

    @Override
    public UserInfoDTO getUserInfo(Long uid) {
        User userInfo = userCache.getUserInfo(uid);
        return UserInfoDTO.builder().uid(uid)
                .name(userInfo.getName())
                .avatar(userInfo.getAvatar()).build();
    }

    @Override
    public List<UserInfoDTO> getUserInfoList(List<Long> uidList) {
        Collection<User> list = userInfoCache.getList(uidList);
        if (list != null && !list.isEmpty()) {
            return list.stream().map(user -> UserInfoDTO.builder()
                    .uid(user.getId())
                    .lastOptTime(user.getLastOptTime())
                    .activeStatus(user.getActiveStatus())
                    .name(user.getName())
                    .avatar(user.getAvatar()).build()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public Map<Long, UserInfoDTO> getUserInfoMap(List<Long> uidList) {
        Collection<User> list = userInfoCache.getList(uidList);
        if (list != null && !list.isEmpty()) {
            return list.stream().map(user -> UserInfoDTO.builder()
                    .uid(user.getId())
                    .lastOptTime(user.getLastOptTime())
                    .activeStatus(user.getActiveStatus())
                    .name(user.getName())
                    .avatar(user.getAvatar()).build()).collect(Collectors.toMap(UserInfoDTO::getUid, Function.identity()));
        }
        return new HashMap<>();
    }

    @Override
    public CursorPageBaseResp<UserInfoDTO> cursorPageUser(UserCursorPageDTO req) {
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
        return CursorPageBaseResp.init(userCursorPage, data);
    }


    @Override
    public List<UserInfoDTO> getAllUser() {
        List<User> memberList = userDao.getMemberList();
        if (memberList.isEmpty()) {
            return Collections.emptyList();
        }
        return memberList.stream().map(user -> UserInfoDTO.builder()
                .uid(user.getId())
                .avatar(user.getAvatar())
                .name(user.getName())
                .activeStatus(user.getActiveStatus())
                .lastOptTime(user.getLastOptTime())
                .build()).collect(Collectors.toList());
    }

    @Override
    public Set<Long> getOnlineUidSet() {
        Set<String> uidStrSet = userCache.getOnlineUidList();
        if (uidStrSet == null) {
            return Collections.emptySet();
        }
        return uidStrSet.stream().map(Long::parseLong).collect(Collectors.toSet());
    }
}
