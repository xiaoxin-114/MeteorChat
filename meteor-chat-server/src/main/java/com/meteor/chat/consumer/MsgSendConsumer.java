package com.meteor.chat.consumer;

import com.meteor.chat.chat.dao.ContactDao;
import com.meteor.chat.chat.dao.RoomDao;
import com.meteor.chat.chat.dao.RoomFriendDao;
import com.meteor.chat.chat.service.ContactService;
import com.meteor.chat.chat.service.cache.*;
import com.meteor.chat.common.domain.dto.MsgSendMessageDTO;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.entity.RoomFriend;
import com.meteor.chat.common.domain.entity.RoomGroup;
import com.meteor.chat.common.domain.enums.RoomTypeEnum;
import com.meteor.chat.common.domain.vo.ChatMessageResp;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.msg.service.MessageService;
import com.meteor.chat.msg.service.impl.MessageServiceImpl;
import com.meteor.chat.route.service.PushService;
import com.meteor.chat.websocket.adapter.WSAdapter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.meteor.chat.rabbitmq.constants.MQConstant;
import javax.annotation.Resource;
import javax.websocket.OnMessage;
import java.util.ArrayList;
import java.util.List;

@Component
public class MsgSendConsumer {

    @Resource
    private MessageDao messageDao;

    @Resource
    private RoomCache roomCache;

    @Resource
    private HotRoomCache hotRoomCache;

    @Resource
    private RoomDao roomDao;

    @Resource
    private PushService pushService;

    @Resource
    private MessageService messageService;

    @Resource
    private ContactDao contactDao;

    @Resource
    private RoomGroupCache roomGroupCache;

    @Resource
    private GroupMemberCache groupMemberCache;

    @Resource
    private RoomFriendCache roomFriendCache;


    @RabbitListener(queues = MQConstant.SEND_MSG_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void consume(MsgSendMessageDTO dto) {
        Long msgId = dto.getMsgId();
        // 此时是推送新消息的，不会有人点赞和点踩，所有不用关心接收用户
        ChatMessageResp messageResp = messageService.getMessageResp(msgId, null);
        Message message = messageDao.getById(msgId);
        Long roomId = message.getRoomId();
        Room room = roomCache.get(roomId);
        // 更新房间的最新活跃时间和消息
        roomDao.refreshActiveTime(message.getRoomId(), message.getCreateTime(), message.getId());
        roomCache.delete(roomId);
        if (room.isHotRoom()) {
            hotRoomCache.refreshActiveTime(roomId, message.getCreateTime());
            pushService.pushMsg(WSAdapter.buildMsgSend(messageResp));
        } else {
            // 根据房间获取需要转发的群聊内的所有用户id
            List<Long> uidList = new ArrayList<>();
            if (RoomTypeEnum.GROUP.getCode().equals(room.getType())) {
                uidList.addAll(groupMemberCache.getMemberUidList(roomId));
            } else {
                RoomFriend roomFriend = roomFriendCache.get(roomId);
                // 发送消息的用户也要推送
                uidList.add(roomFriend.getUid1());
                uidList.add(roomFriend.getUid2());
            }
            // 更新用户contact表格的最新活跃时间与最新消息id
            contactDao.refreshActiveTime(roomId, uidList, message.getCreateTime(), message.getId());
            // 推送消息
            pushService.pushMsg(WSAdapter.buildMsgSend(messageResp), uidList);
        }
    }

}
