package com.meteor.chat.room.consumer;

import com.meteor.chat.api.msg.MessageCommonApi;
import com.meteor.chat.api.msg.dto.ChatMessageResp;
import com.meteor.chat.api.msg.dto.MsgSendMessageDTO;
import com.meteor.chat.api.room.enums.RoomTypeEnum;
import com.meteor.chat.push.common.core.push.PushService;
import com.meteor.chat.rabbitmq.core.constants.MQConstant;
import com.meteor.chat.room.adapter.WSAdapter;
import com.meteor.chat.room.dao.ContactDao;
import com.meteor.chat.room.dao.RoomDao;
import com.meteor.chat.room.domain.entity.Room;
import com.meteor.chat.room.domain.entity.RoomFriend;
import com.meteor.chat.room.service.cache.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class MsgSendConsumer {

    @Resource
    private MessageCommonApi messageCommonApi;

    @Resource
    private RoomCache roomCache;

    @Resource
    private HotRoomCache hotRoomCache;

    @Resource
    private RoomDao roomDao;

    @Resource
    private PushService pushService;

    @Resource
    private ContactDao contactDao;

    @Resource
    private GroupMemberCache groupMemberCache;

    @Resource
    private RoomFriendCache roomFriendCache;


    @RabbitListener(queues = MQConstant.SEND_MSG_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void consume(MsgSendMessageDTO dto) {
        Long msgId = dto.getMsgId();
        // 此时是推送新消息的，不会有人点赞和点踩，所有不用关心接收用户
        ChatMessageResp messageResp = messageCommonApi.getMessageResp(msgId).getCheckData();
        ChatMessageResp.Message message = messageResp.getMessage();
        Long roomId = message.getRoomId();
        Room room = roomCache.get(roomId);
        // 更新房间的最新活跃时间和消息
        roomDao.refreshActiveTime(message.getRoomId(), message.getSendTime(), message.getId());
        roomCache.delete(roomId);
        if (room.isHotRoom()) {
            hotRoomCache.refreshActiveTime(roomId, message.getSendTime());
            pushService.pushRoomMsg(WSAdapter.buildMsgSend(messageResp));
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
            contactDao.refreshActiveTime(roomId, uidList, message.getSendTime(), message.getId());
            // 推送消息
            pushService.pushRoomMsg(WSAdapter.buildMsgSend(messageResp), uidList);
        }
    }

}
