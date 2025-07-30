package com.meteor.chat.msg.adapter;

import com.meteor.chat.msg.domain.dto.MessageRecallDTO;
import com.meteor.chat.msg.domain.dto.MsgMarkDTO;
import com.meteor.chat.msg.domain.vo.WSMsgRecall;
import com.meteor.chat.push.common.domain.enums.WSRespTypeEnum;
import com.meteor.chat.push.common.domain.vo.WSBaseResp;
import com.meteor.chat.msg.domain.vo.WSMsgMark;

import java.util.Collections;

public class WSAdapter {

    public static WSBaseResp<WSMsgRecall> buildMsgRecall(MessageRecallDTO dto) {
        WSMsgRecall wsMsgRecall = new WSMsgRecall();
        wsMsgRecall.setMsgId(dto.getMsgId());
        wsMsgRecall.setRoomId(dto.getRoomId());
        wsMsgRecall.setRecallUid(dto.getRecallUid());
        return new WSBaseResp<>(WSRespTypeEnum.RECALL.getType(), wsMsgRecall);
    }

    public static WSBaseResp<WSMsgMark> buildMsgMarkResp(MsgMarkDTO msgMarkDTO, Integer markCount) {
        WSMsgMark wsMsgMark = new WSMsgMark();
        WSMsgMark.WSMsgMarkItem wsMsgMarkItem = new WSMsgMark.WSMsgMarkItem();
        wsMsgMarkItem.setUid(msgMarkDTO.getUid());
        wsMsgMarkItem.setMarkType(msgMarkDTO.getMarkType());
        wsMsgMarkItem.setMarkCount(markCount);
        wsMsgMarkItem.setActType(msgMarkDTO.getActType());
        wsMsgMarkItem.setMsgId(msgMarkDTO.getMsgId());
        wsMsgMark.setMarkList(Collections.singletonList(wsMsgMarkItem));
        return new WSBaseResp<>(WSRespTypeEnum.MARK.getType(), wsMsgMark);
    }
}
