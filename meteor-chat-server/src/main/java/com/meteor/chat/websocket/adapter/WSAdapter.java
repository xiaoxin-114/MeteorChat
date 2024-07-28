package com.meteor.chat.websocket.adapter;
import java.util.Collections;
import java.util.Date;

import com.meteor.chat.chat.service.adapter.RoomAdapter;
import com.meteor.chat.common.domain.dto.MessageRecallDTO;
import com.meteor.chat.common.domain.entity.GroupMember;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.enums.ChatActiveStatusEnum;
import com.meteor.chat.common.domain.vo.ChatMessageResp;
import com.meteor.chat.common.domain.vo.GroupMemberResp;
import com.meteor.chat.websocket.domain.enums.WSRespTypeEnum;
import com.meteor.chat.websocket.domain.vo.*;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WSAdapter {

    public static WSBaseResp<WSLoginUrl> buildLoginResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        wsBaseResp.setData(WSLoginUrl.builder().loginUrl(wxMpQrCodeTicket.getUrl()).build());
        return wsBaseResp;
    }

    public static WSBaseResp buildScanSuccessResp() {
        WSBaseResp<Object> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }

    public static WSBaseResp<WSLoginSuccess> buildLoginSuccessResp(User user, String token, Long power) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        WSLoginSuccess loginSuccess = WSLoginSuccess.builder()
                .avatar(user.getAvatar())
                .name(user.getName())
                .power(power == null ? 0 : power)
                .uid(user.getId())
                .token(token)
                .build();
        resp.setData(loginSuccess);
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        return resp;
    }

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



    public static WSBaseResp<?> buildTokenInvalidResp() {
        WSBaseResp<Object> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return wsBaseResp;
    }

    public static WSBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp messageResp) {
        WSBaseResp<ChatMessageResp> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setData(messageResp);
        wsBaseResp.setType(WSRespTypeEnum.MESSAGE.getType());
        return wsBaseResp;
    }

    public static WSBaseResp<WSMemberChange> buildGroupMemberAdd(Long roomId, User user) {
        WSBaseResp<WSMemberChange> wsResp = new WSBaseResp<>();
        WSMemberChange wsMemberChange = new WSMemberChange();
        wsResp.setData(wsMemberChange);
        wsResp.setType(WSRespTypeEnum.MEMBER_CHANGE.getType());
        wsMemberChange.setUid(user.getId());
        wsMemberChange.setRoomId(roomId);
        wsMemberChange.setChangeType(WSMemberChange.CHANGE_TYPE_ADD);
        wsMemberChange.setLastOptTime(user.getLastOptTime());
        wsMemberChange.setActiveStatus(user.getActiveStatus());
        return wsResp;
    }

    public static WSBaseResp<WSMemberChange> buildGroupMemberRemove(Long roomId, Long uid) {
        WSBaseResp<WSMemberChange> wsResp = new WSBaseResp<>();
        WSMemberChange wsMemberChange = new WSMemberChange();
        wsResp.setData(wsMemberChange);
        wsResp.setType(WSRespTypeEnum.MEMBER_CHANGE.getType());
        wsMemberChange.setUid(uid);
        wsMemberChange.setRoomId(roomId);
        wsMemberChange.setChangeType(WSMemberChange.CHANGE_TYPE_REMOVE);
        return wsResp;
    }

    public static WSBaseResp<WSMsgRecall> buildMsgRecall(MessageRecallDTO dto) {
        WSMsgRecall wsMsgRecall = new WSMsgRecall();
        wsMsgRecall.setMsgId(dto.getMsgId());
        wsMsgRecall.setRoomId(dto.getRoomId());
        wsMsgRecall.setRecallUid(dto.getRecallUid());
        return new WSBaseResp<>(WSRespTypeEnum.RECALL.getType(), wsMsgRecall);
    }
}
