package com.meteor.chat.room.adapter;

import com.meteor.chat.api.msg.dto.ChatMessageResp;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.push.common.domain.enums.WSRespTypeEnum;
import com.meteor.chat.push.common.domain.vo.WSBaseResp;
import com.meteor.chat.room.domain.vo.WSMemberChange;

public class WSAdapter {

    public static WSBaseResp<ChatMessageResp> buildMsgSend(ChatMessageResp messageResp) {
        WSBaseResp<ChatMessageResp> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setData(messageResp);
        wsBaseResp.setType(WSRespTypeEnum.MESSAGE.getType());
        return wsBaseResp;
    }

    public static WSBaseResp<WSMemberChange> buildGroupMemberAdd(Long roomId, UserInfoDTO user) {
        WSBaseResp<WSMemberChange> wsResp = new WSBaseResp<>();
        WSMemberChange wsMemberChange = new WSMemberChange();
        wsResp.setData(wsMemberChange);
        wsResp.setType(WSRespTypeEnum.MEMBER_CHANGE.getType());
        wsMemberChange.setUid(user.getUid());
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
}
