package com.meteor.chat.msg.event.listener;


import com.meteor.chat.api.room.RoomCommonApi;
import com.meteor.chat.api.room.RoomMemberCommonApi;
import com.meteor.chat.api.room.dto.RoomInfoDTO;
import com.meteor.chat.msg.adapter.WSAdapter;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.msg.dao.MessageMarkDao;
import com.meteor.chat.msg.domain.dto.MsgMarkDTO;
import com.meteor.chat.msg.domain.entity.Message;
import com.meteor.chat.msg.enums.MessageMarkActTypeEnum;
import com.meteor.chat.msg.event.MsgMarkEvent;
import com.meteor.chat.push.common.core.push.PushService;
import com.meteor.chat.push.common.domain.vo.WSBaseResp;
import com.meteor.chat.msg.domain.vo.WSMsgMark;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Async
@Component
public class MsgMarkEventListener {

    @Resource
    private PushService pushService;

    @Resource
    private MessageDao messageDao;

    @Resource
    private RoomCommonApi roomCommonApi;

    @Resource
    private RoomMemberCommonApi roomMemberCommonApi;

    @Resource
    private MessageMarkDao messageMarkDao;

    /**
     * 发送反馈消息推送给群聊在线用户
     * @param event
     */
    @TransactionalEventListener(value = MsgMarkEvent.class, fallbackExecution = true)
    public void sendMark(MsgMarkEvent event) {
        MsgMarkDTO msgMarkDTO = event.getMsgMarkDTO();
        Message message = messageDao.getById(msgMarkDTO.getMsgId());
        RoomInfoDTO room = roomCommonApi.getRoomInfo(message.getRoomId()).getCheckData();
        Integer markCount = messageMarkDao.countMsgType(msgMarkDTO.getMsgId(), msgMarkDTO.getMarkType());
        WSBaseResp<WSMsgMark> wsBaseResp = WSAdapter.buildMsgMarkResp(msgMarkDTO, markCount);
        if (room.isHotRoom()) {
            pushService.pushRoomMsg(wsBaseResp);
        }else {
            List<Long> uidList = roomMemberCommonApi.getMemberList(message.getRoomId()).getCheckData();
            pushService.pushRoomMsg(wsBaseResp, uidList);
        }
    }

    /**
     * 如果消息点赞数超过一定数量发送徽章
     * @param event
     */
    @TransactionalEventListener(value = MsgMarkEvent.class, fallbackExecution = true)
    public void assignItem(MsgMarkEvent event) {
        MsgMarkDTO msgMarkDTO = event.getMsgMarkDTO();
        // 取消的话就不需要处理
        if (Objects.equals(msgMarkDTO.getActType(), MessageMarkActTypeEnum.UN_MARK.getType())) {
            return;
        }
        // todo
    }
}
