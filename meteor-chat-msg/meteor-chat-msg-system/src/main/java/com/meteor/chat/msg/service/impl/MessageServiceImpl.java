package com.meteor.chat.msg.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.meteor.chat.api.msg.dto.ChatMessageResp;
import com.meteor.chat.api.msg.dto.MemberAddMsgDTO;
import com.meteor.chat.api.msg.dto.RoomMsgDTO;
import com.meteor.chat.api.msg.dto.RoomMsgReqDTO;
import com.meteor.chat.api.room.ContactCommonApi;
import com.meteor.chat.api.room.RoomCommonApi;
import com.meteor.chat.api.room.RoomMemberCommonApi;
import com.meteor.chat.api.room.dto.*;
import com.meteor.chat.api.room.enums.RoomTypeEnum;
import com.meteor.chat.api.user.UserInfoCommonApi;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.api.msg.dto.MsgSendMessageDTO;
import com.meteor.chat.msg.domain.entity.Message;
import com.meteor.chat.msg.domain.entity.MessageExtra;
import com.meteor.chat.msg.domain.entity.MessageMark;
import com.meteor.chat.msg.domain.entity.MsgRecall;
import com.meteor.chat.msg.domain.vo.*;
import com.meteor.chat.msg.enums.MessageTypeEnum;
import com.meteor.chat.msg.enums.ReadEnum;
import com.meteor.chat.rabbitmq.core.constants.MQConstant;
import com.meteor.chat.rabbitmq.core.producer.MQProducer;
import com.meteor.chat.redis.core.annotation.RedissonLock;
import com.meteor.chat.msg.domain.dto.MessageRecallDTO;
import com.meteor.chat.msg.domain.dto.MsgReadInfoDTO;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.msg.event.MessageRecallEvent;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.msg.dao.MessageMarkDao;
import com.meteor.chat.msg.service.MessageService;
import com.meteor.chat.msg.adapter.MsgAdapter;
import com.meteor.chat.msg.service.handler.msg.AbstractMsgHandler;
import com.meteor.chat.msg.service.handler.msg.MsgHandlerFactory;
import com.meteor.chat.msg.service.handler.msgmark.AbstractMsgMarkHandler;
import com.meteor.chat.msg.service.handler.msgmark.MsgMarkHandlerFacroty;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageDao messageDao;

    @Resource
    private ContactCommonApi contactCommonApi;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private RoomCommonApi roomCommonApi;

    @Resource
    private RoomMemberCommonApi roomMemberCommonApi;

    @Resource
    private MessageMarkDao messageMarkDao;

    @Resource
    private UserInfoCommonApi userInfoCommonApi;

    @Resource
    private MQProducer mqProducer;

    @Override
    public CursorPageBaseResp<ChatMessageReadResp> cursorPageMsgReader(MessageReadCursorPageReq req) {
        Long msgId = req.getMsgId();
        Message message = messageDao.getById(msgId);
        CursorPageBaseResp<ContactInfoDTO> contactPage;
        MessageReadCursorPageDTO cursorPageDTO = MessageReadCursorPageDTO.builder()
                .cursor(req.getCursor())
                .pageSize(req.getPageSize())
                .roomId(message.getRoomId())
                .msgCreateTime(message.getCreateTime()).build();
        // 获取未读的contact列表
        if (ReadEnum.UNREAD.getCode().equals(req.getSearchType())) {
            contactPage  = contactCommonApi.cursorUnReadPage(cursorPageDTO).getCheckData();
        }else {
            contactPage = contactCommonApi.cursorReadPage(cursorPageDTO).getCheckData();
        }
        List<ChatMessageReadResp> uidList = contactPage.getList().stream()
                // 过滤掉发送消息的用户
                .filter(contact -> !message.getFromUid().equals(contact.getUid()))
                .map(contact -> new ChatMessageReadResp(contact.getUid()))
                .collect(Collectors.toList());
        return CursorPageBaseResp.init(contactPage, uidList);
    }

    @Override
    public List<MsgReadInfoDTO> countReadAndUnRead(MessageReadInfoReq req, Long uid) {
        List<Long> idList = req.getMsgIds();
        List<Message> msgList = messageDao.listByIds(idList);
        Assert.assertTrue("只能查询自己发送的消息阅读数", msgList.stream().allMatch(msg -> uid.equals(msg.getFromUid())));
        List<Long> roomIds = msgList.stream().map(Message::getRoomId).distinct().collect(Collectors.toList());
        Assert.assertTrue("只能查询同一会话下的消息", roomIds.size() == 1);
        List<ContactInfoDTO> contactList = contactCommonApi.listByRoomId(roomIds.get(0), uid).getCheckData();
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
        if (!CommonConstants.SYSTEM_UID.equals(uid)) {
            // 系统消息以外的需要校验
            checkSendMsg(request, uid);
        }
        AbstractMsgHandler msgHandler = MsgHandlerFactory.getStrategyNotNull(request.getMsgType());
        Long msgId = msgHandler.handlerMsg(request, uid);
        if (Objects.nonNull(msgId)) {
            // 方法进行了封装，使用mq发送操作可以放在事务中
            // 其实该方法的事务内操作只是将调用数据存放到本地消息表中
            // mq的发送消息操作是在事务提交后进行的
            mqProducer.sendSecureMsg(MQConstant.SEND_MSG_EXCHANGE, MQConstant.SEND_MSG_ROUTING_KEY, new MsgSendMessageDTO(msgId));
        }
        return msgId;
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> cursorChatMessageResp(MessageCursorReq req, Long uid) {
        Long roomId = req.getRoomId();
        boolean inRoom = inRoom(roomId, uid);
        Long lastMsgId = null;
        if (!inRoom) {
            // 如果用户不在群聊，则只会显示历史信息，不会显示最新消息
            ContactInfoDTO contact = contactCommonApi.getByRoomIdUid(roomId, uid).getCheckData();
            Assert.assertNotNull("数据异常", contact);
            lastMsgId = contact.getLastMsgId();
        }
        CursorPageBaseResp<Message> messageCursorPage = messageDao.cursorMessage(req, roomId, lastMsgId);
        if (messageCursorPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        Set<String> blackList = userInfoCommonApi.getBlackList().getCheckData();
        if (blackList == null) {
            blackList = new HashSet<>();
        }
        List<Message> messageList = messageCursorPage.getList();
        // 过滤掉被拉黑用户的信息
        Iterator<Message> iterator = messageList.iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            if (blackList.contains(message.getFromUid())) {
                iterator.remove();
            }
        }
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
            boolean hasRoomPower = roomCommonApi.hasRoomPower(uid, req.getRoomId()).getCheckData();
            Assert.assertTrue("用户没有权限操作", hasRoomPower);
        }
        Assert.assertFalse("发出超出2分钟的消息无法撤回", message.getCreateTime().before(DateUtil.offsetMinute(new Date(), -2)));
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
    public void readMsg(ChatMsgReadReq req, Long uid) {
        Long roomId = req.getRoomId();
        contactCommonApi.readMsg(ReadMessageDTO.builder().roomId(roomId).uid(uid).build());
    }


    @Override
    public List<RoomMsgDTO> getRoomMsgList(List<RoomMsgReqDTO> reqList) {
        if (CollectionUtils.isEmpty(reqList)) {
            return Collections.emptyList();
        }
        List<Long> msgIdList = reqList.stream().map(RoomMsgReqDTO::getLastMsgId).collect(Collectors.toList());
        List<Message> msgList = messageDao.listByIds(msgIdList);
        if (CollectionUtils.isEmpty(msgList)) {
            return Collections.emptyList();
        }
        Map<Long, Message> msgMap = msgList.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        return reqList.stream().map(req -> {
            Message message = msgMap.get(req.getLastMsgId());
            RoomMsgDTO roomMsgDTO = RoomMsgDTO.builder()
                    .lastMsgId(req.getLastMsgId())
                    .unreadCount(messageDao.countUnReadMsg(req.getRoomId(), req.getReadTime()))
                    .roomId(req.getRoomId())
                    .build();
            if (message == null) {
                return roomMsgDTO;
            }
            AbstractMsgHandler msgHandler = MsgHandlerFactory.getStrategyNotNull(message.getType());
            roomMsgDTO.setMsgText(msgHandler.messageText(message));
            return roomMsgDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public void removeRoomMsg(Long roomId) {
        messageDao.removeByRoomId(roomId);
    }

    @Override
    @RedissonLock(key = "#uid + '_' + #req.msgId")
    public void markMsg(MsgMarkReq req, Long uid) {
        AbstractMsgMarkHandler handler = MsgMarkHandlerFacroty.getOrDefault(req.getMarkType());
        Assert.assertNotNull("标记类型异常", handler);
        handler.doMark(req, uid);
    }

    @Override
    public void sendMemberAddMsg(MemberAddMsgDTO memberAddMsgDTO) {
        memberAddMsgDTO.getMemberUidList().add(memberAddMsgDTO.getInviter());
        List<UserInfoDTO> userInfoList = userInfoCommonApi.getUserInfoList(memberAddMsgDTO.getMemberUidList()).getCheckData();
        if (CollectionUtils.isEmpty(userInfoList)) {
            return;
        }
        String inviteName = "";
        List<String> memberNameList = new ArrayList<>();
        for (UserInfoDTO userInfo : userInfoList) {
            if (Objects.equals(userInfo.getUid(), memberAddMsgDTO.getInviter())) {
                inviteName = userInfo.getName();
            } else {
                memberNameList.add(userInfo.getName());
            }
        }
        sendMsg(MsgAdapter.buildInviteSuccessMsg(memberAddMsgDTO.getRoomId(), inviteName, memberNameList), CommonConstants.SYSTEM_UID);
    }

    @Override
    public void sendMemberSubMsg(Long roomId, Long uid, String content) {
        UserInfoDTO userInfo = userInfoCommonApi.getUserInfo(uid).getCheckData();
        if (Objects.nonNull(userInfo) && Objects.equals(userInfo.getUid(), uid)) {
            sendMsg(MsgAdapter.buildMemberChange(roomId, userInfo.getName() + content), CommonConstants.SYSTEM_UID);
        }
    }

    private void checkSendMsg(ChatMessageReq request, Long uid) {
        Long roomId = request.getRoomId();
        RoomInfoDTO room = roomCommonApi.getRoomInfo(roomId).getCheckData();
        Assert.assertNotNull("房间号有误", room);
        if (room.isHotRoom()) {
            // 全员群所有用户都在
            return;
        }
        if (RoomTypeEnum.GROUP.getCode().equals(room.getType())) {
            // 群聊需要校验用户是否在群里
            List<Long> memberUidList = roomMemberCommonApi.getMemberList(roomId).getCheckData();
            Assert.assertTrue("您已被移出群聊", memberUidList.contains(uid));
        }else if (RoomTypeEnum.SINGLE.getCode().equals(room.getType())) {
            RoomFriendDTO roomFriend = roomCommonApi.getRoomFriend(roomId).getCheckData();
            Assert.assertNotNull("您已被对方拉黑", roomFriend);
            Assert.assertTrue("您已被对方拉黑", roomFriend.hasUid(uid));
        }

    }

    private boolean inRoom(Long roomId, Long uid) {
        RoomInfoDTO room = roomCommonApi.getRoomInfo(roomId).getCheckData();
        Assert.assertNotNull("房间号有误", room);
        if (room.isHotRoom()) {
            // 全员群所有用户都在
            return true;
        }
        if (RoomTypeEnum.GROUP.getCode().equals(room.getType())) {
            // 群聊需要校验用户是否在群里
            List<Long> memberUidList = roomMemberCommonApi.getMemberList(roomId).getCheckData();
            Assert.assertNotNull("数据异常", memberUidList);
            return memberUidList.contains(uid);
        }else if (RoomTypeEnum.SINGLE.getCode().equals(room.getType())) {
            RoomFriendDTO roomFriend = roomCommonApi.getRoomFriend(roomId).getCheckData();
            return Objects.nonNull(roomFriend) && roomFriend.hasUid(uid);
        }
        throw new BusinessException("数据异常");
    }
}
