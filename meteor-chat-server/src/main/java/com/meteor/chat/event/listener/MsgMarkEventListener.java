package com.meteor.chat.event.listener;

import com.meteor.chat.chat.service.cache.GroupMemberCache;
import com.meteor.chat.chat.service.cache.RoomCache;
import com.meteor.chat.common.domain.dto.MsgMarkDTO;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.enums.MessageMarkActTypeEnum;
import com.meteor.chat.common.domain.enums.MessageMarkTypeEnum;
import com.meteor.chat.event.MsgMarkEvent;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.msg.dao.MessageMarkDao;
import com.meteor.chat.route.service.PushService;
import com.meteor.chat.websocket.adapter.WSAdapter;
import com.meteor.chat.websocket.domain.vo.WSBaseResp;
import com.meteor.chat.websocket.domain.vo.WSMsgMark;
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
    private GroupMemberCache groupMemberCache;

    @Resource
    private RoomCache roomCache;

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
        Room room = roomCache.get(message.getRoomId());
        Integer markCount = messageMarkDao.countMsgType(msgMarkDTO.getMsgId(), msgMarkDTO.getMarkType());
        WSBaseResp<WSMsgMark> wsBaseResp = WSAdapter.buildMsgMarkResp(msgMarkDTO, markCount);
        if (room.isHotRoom()) {
            pushService.pushMsg(wsBaseResp);
        }else {
            List<Long> uidList = groupMemberCache.getMemberUidList(message.getRoomId());
            pushService.pushMsg(wsBaseResp, uidList);
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
