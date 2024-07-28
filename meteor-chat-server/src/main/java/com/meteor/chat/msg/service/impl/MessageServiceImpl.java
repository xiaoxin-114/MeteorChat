package com.meteor.chat.msg.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import com.meteor.chat.chat.dao.ContactDao;
import com.meteor.chat.chat.dao.RoomFriendDao;
import com.meteor.chat.chat.service.RoomService;
import com.meteor.chat.chat.service.adapter.RoomAdapter;
import com.meteor.chat.chat.service.cache.GroupMemberCache;
import com.meteor.chat.chat.service.cache.RoomCache;
import com.meteor.chat.chat.service.cache.RoomGroupCache;
import com.meteor.chat.common.annotation.RedissonLock;
import com.meteor.chat.common.domain.dto.MessageRecallDTO;
import com.meteor.chat.common.domain.dto.MsgReadInfoDTO;
import com.meteor.chat.common.domain.entity.*;
import com.meteor.chat.common.domain.enums.*;
import com.meteor.chat.common.domain.vo.ChatMessageReadResp;
import com.meteor.chat.common.domain.vo.ChatMessageResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.*;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.common.exception.CommonErrorEnum;
import com.meteor.chat.common.util.CursorUtils;
import com.meteor.chat.event.MessageRecallEvent;
import com.meteor.chat.event.MessageSendEvent;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.msg.dao.MessageMarkDao;
import com.meteor.chat.msg.service.MessageService;
import com.meteor.chat.msg.service.adapter.MsgAdapter;
import com.meteor.chat.msg.service.handler.AbstractMsgHandler;
import com.meteor.chat.msg.service.handler.MsgHandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageDao messageDao;

    @Resource
    private ContactDao contactDao;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private RoomCache roomCache;

    @Resource
    private RoomGroupCache roomGroupCache;

    @Resource
    private GroupMemberCache groupMemberCache;

    @Resource
    private RoomFriendDao roomFriendDao;

    @Resource
    private MessageMarkDao messageMarkDao;

    @Resource
    private RoomService roomService;

    @Override
    public CursorPageBaseResp<ChatMessageReadResp> cursorPageMsgReader(MessageReadCursorPageReq req) {
        Long msgId = req.getMsgId();
        Message message = messageDao.getById(msgId);
        CursorPageBaseResp<Contact> contactPage;
        // 获取未读的contact列表
        if (ReadEnum.UNREAD.getCode().equals(req.getSearchType())) {
            contactPage  = contactDao.cursorUnReadPage(req, message.getRoomId(), message.getCreateTime());
        }else {
            contactPage = contactDao.cursorReadPage(req, message.getRoomId(), message.getCreateTime());
        }
        List<Long> uidList = contactPage.getList().stream().map(Contact::getUid)
                .filter(id -> !message.getFromUid().equals(id)).collect(Collectors.toList());
        return CursorPageBaseResp.init(contactPage, uidList);
    }

    @Override
    public List<MsgReadInfoDTO> countReadAndUnRead(MessageReadInfoReq req, Long uid) {
        List<Long> idList = req.getMsgIds();
        List<Message> msgList = messageDao.listByIds(idList);
        msgList.forEach(msg -> {
            if (!uid.equals(msg.getFromUid())) {
                throw new BusinessException("只能查询自己发送的消息阅读数");
            }
        });
        List<Long> roomIds = msgList.stream().map(Message::getRoomId).distinct().collect(Collectors.toList());
        Assert.assertTrue("只能查询同一会话下的消息", roomIds.size() == 1);
        List<Contact> contactList = contactDao.listByRoomId(roomIds.get(0), uid);
        if (CollectionUtils.isEmpty(contactList)) {
            throw new BusinessException("会话信息缺失，计算失败");
        }
        return msgList.stream()
                .map(msg -> MsgAdapter.buildMsgReadInfoDTO(msg, contactList))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendMsg(ChatMessageReq request, Long uid) {
        checkSendMsg(request, uid);
        AbstractMsgHandler msgHandler = MsgHandlerFactory.getStrategyNotNull(request.getMsgType());
        Long msgId = msgHandler.handlerMsg(request, uid);
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));
        return msgId;
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> cursorChatMessageResp(MessageCursorReq req, Long uid) {
        Long roomId = req.getRoomId();
        boolean inRoom = inRoom(roomId, uid);
        Long lastMsgId = null;
        if (!inRoom) {
            // 如果用户不在群聊，则只会显示历史信息，不会显示最新消息
            Contact contact = contactDao.getByUidAndRoomId(roomId, uid);
            Assert.assertNotNull("数据异常", contact);
            lastMsgId = contact.getLastMsgId();
        }
        CursorPageBaseResp<Message> messageCursorPage = messageDao.cursorMessage(req, roomId, lastMsgId);
        if (messageCursorPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        List<Message> messageList = messageCursorPage.getList();
        List<MessageMark> messageMarkList = messageMarkDao.listByMsgIdList(messageList.stream().map(Message::getId).collect(Collectors.toList()));
        return CursorPageBaseResp.init(messageCursorPage, MsgAdapter.buildChatMessageResp(messageList, messageMarkList, uid));
    }

    @Override
    public ChatMessageResp getMessageResp(Long msgId, Long receiveUid) {
        Message message = messageDao.getById(msgId);
        Assert.assertNotNull("消息id异常", message);
        List<MessageMark> messageMarkList = messageMarkDao.listByMsgId(msgId);
        List<ChatMessageResp> chatMessageResps = MsgAdapter.buildChatMessageResp(Collections.singletonList(message), messageMarkList, receiveUid);
        return CollUtil.getFirst(chatMessageResps);
    }


    @Override
    public void recall(MsgRecallReq req, Long uid) {
        Message message = messageDao.getById(req.getMsgId());
        // 如果不是消息发送者撤回消息，就必须得是管理员
        if (!Objects.equals(message.getFromUid(), uid)) {
            boolean hasRoomPower = roomService.hasRoomPower(uid, req.getRoomId());
            Assert.assertTrue("用户没有权限操作", hasRoomPower);
        }
        Assert.assertTrue("发出超出2分钟的消息无法撤回", message.getCreateTime().before(DateUtil.offsetMinute(new Date(), -2)));
        // 修改消息信息
        MessageExtra extra = message.getExtra();
        MsgRecall msgRecall = MsgRecall.builder().recallUid(uid).recallTime(new Date()).build();
        extra.setRecall(msgRecall);
        Message update = new Message();
        update.setId(message.getId());
        update.setType(MessageTypeEnum.RECALL.getType());
        update.setExtra(extra);
        messageDao.updateById(update);
        applicationEventPublisher.publishEvent(new MessageRecallEvent(new MessageRecallDTO(req.getMsgId(), req.getRoomId(), uid), this));
    }

    @Override
    @RedissonLock(key = "#uid")
    public void readMsg(ChatMessageMemberReq req, Long uid) {
        Long roomId = req.getRoomId();
        Room room = roomCache.get(roomId);
        Assert.assertNotNull("房间号异常", room);
        Contact contact = contactDao.getByUidAndRoomId(roomId, uid);
        if (Objects.isNull(contact)) {
            Contact insert = new Contact();
            insert.setUid(uid);
            insert.setRoomId(roomId);
            insert.setReadTime(new Date());
            insert.setActiveTime(room.getActiveTime());
            // 其余消息在群聊发送消息后自然会更新
            contactDao.save(insert);
        } else {
            Contact update = new Contact();
            update.setId(contact.getId());
            update.setReadTime(new Date());
            contactDao.updateById(update);
        }
    }

    private void checkSendMsg(ChatMessageReq request, Long uid) {
        Long roomId = request.getRoomId();
        Room room = roomCache.get(roomId);
        Assert.assertNotNull("房间号有误", room);
        if (room.isHotRoom()) {
            // 全员群所有用户都在
            return;
        }
        if (RoomTypeEnum.GROUP.getCode().equals(room.getType())) {
            // 群聊需要校验用户是否在群里
            List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
            Assert.assertTrue("您已被移出群聊", memberUidList.contains(uid));
        }else if (RoomTypeEnum.SINGLE.getCode().equals(room.getType())) {
            List<RoomFriend> roomFriends = roomFriendDao.listByRoomIds(Collections.singletonList(roomId));
            Assert.assertTrue("数据异常", roomFriends.size() == 1);
            RoomFriend roomFriend = roomFriends.get(0);
            Assert.assertEquals("您已被对方拉黑", RoomFriendStatusEnum.NORAML.getCode(), roomFriend.getStatus());
            Assert.assertTrue("您已被对方拉黑", roomFriend.hasUser(uid));
        }

    }

    private boolean inRoom(Long roomId, Long uid) {
        Room room = roomCache.get(roomId);
        Assert.assertNotNull("房间号有误", room);
        if (room.isHotRoom()) {
            // 全员群所有用户都在
            return true;
        }
        if (RoomTypeEnum.GROUP.getCode().equals(room.getType())) {
            // 群聊需要校验用户是否在群里
            List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
            Assert.assertNotNull("数据异常", memberUidList);
            return memberUidList.contains(uid);
        }else if (RoomTypeEnum.SINGLE.getCode().equals(room.getType())) {
            List<RoomFriend> roomFriends = roomFriendDao.listByRoomIds(Collections.singletonList(roomId));
            Assert.assertTrue("数据异常", roomFriends.size() == 1);
            RoomFriend roomFriend = roomFriends.get(0);
            return RoomFriendStatusEnum.NORAML.getCode().equals(roomFriend.getStatus()) || roomFriend.hasUser(uid);
        }
        throw new BusinessException("数据异常");
    }
}
