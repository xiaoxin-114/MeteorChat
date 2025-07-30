package com.meteor.chat.user.adapter;

import com.meteor.chat.push.common.domain.enums.WSRespTypeEnum;
import com.meteor.chat.push.common.domain.vo.WSBaseResp;
import com.meteor.chat.push.common.domain.vo.WSFriendApply;
import com.meteor.chat.user.domain.entity.User;
import com.meteor.chat.user.domain.vo.GroupMemberResp;
import com.meteor.chat.user.domain.vo.ws.WSOnlineOfflineNotify;
import com.meteor.chat.user.enums.ChatActiveStatusEnum;

import java.util.Collections;

public class WSAdapter {


    public static WSBaseResp<WSOnlineOfflineNotify> buildUserOnlineResp(User user, Long onlineNum) {
        WSBaseResp<WSOnlineOfflineNotify> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.ONLINE_OFFLINE_NOTIFY.getType());
        wsBaseResp.setData(buildOnlineOfflineNotify(user, ChatActiveStatusEnum.ONLINE.getStatus(), onlineNum));
        return wsBaseResp;
    }

    public static WSBaseResp<WSOnlineOfflineNotify> buildUserOfflineResp(User user, Long onlineNum) {
        WSBaseResp<WSOnlineOfflineNotify> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.ONLINE_OFFLINE_NOTIFY.getType());
        wsBaseResp.setData(buildOnlineOfflineNotify(user, ChatActiveStatusEnum.OFFLINE.getStatus(), onlineNum));
        return wsBaseResp;
    }

    private static WSOnlineOfflineNotify buildOnlineOfflineNotify(User user, Integer type, Long onlineNum) {
        WSOnlineOfflineNotify notify = new WSOnlineOfflineNotify();
        GroupMemberResp groupMemberResp = new GroupMemberResp();
        groupMemberResp.setUid(user.getId());
        groupMemberResp.setActiveStatus(type);
        groupMemberResp.setLastOptTime(user.getLastOptTime());
        notify.setChangeList(Collections.singletonList(groupMemberResp));
        notify.setOnlineNum(onlineNum);
        return notify;
    }

    public static WSBaseResp<WSFriendApply> buildFriendApply(Long uid, Integer unreadCount) {
        WSFriendApply wsFriendApply = new WSFriendApply(uid, unreadCount);
        return new WSBaseResp<WSFriendApply>(WSRespTypeEnum.APPLY.getType(), wsFriendApply);
    }
}
