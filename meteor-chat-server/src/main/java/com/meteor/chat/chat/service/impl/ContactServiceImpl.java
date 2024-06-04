package com.meteor.chat.chat.service.impl;

import cn.hutool.core.lang.Pair;
import com.meteor.chat.chat.dao.ContactDao;
import com.meteor.chat.chat.dao.RoomFriendDao;
import com.meteor.chat.chat.service.ContactService;
import com.meteor.chat.chat.service.adapter.RoomAdapter;
import com.meteor.chat.chat.service.cache.HotRoomCache;
import com.meteor.chat.chat.service.cache.RoomCache;
import com.meteor.chat.chat.service.cache.RoomFriendCache;
import com.meteor.chat.chat.service.cache.RoomGroupCache;
import com.meteor.chat.common.domain.dto.ChatRoomDTO;
import com.meteor.chat.common.domain.entity.*;
import com.meteor.chat.common.domain.enums.RoomTypeEnum;
import com.meteor.chat.common.domain.vo.ChatRoomResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.ContactFriendReq;
import com.meteor.chat.common.domain.vo.req.CursorPageBaseReq;
import com.meteor.chat.common.domain.vo.req.IdBaseReq;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.user.service.cache.UserCache;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {

    @Resource
    private HotRoomCache hotRoomCache;

    @Resource
    private ContactDao contactDao;

    @Resource
    private RoomCache roomCache;

    @Resource
    private RoomFriendCache roomFriendCache;

    @Resource
    private RoomGroupCache roomGroupCache;

    @Resource
    private UserCache userCache;

    @Resource
    private MessageDao messageDao;

    @Resource
    private RoomFriendDao roomFriendDao;

    @Override
    public CursorPageBaseResp<ChatRoomResp> pageChatRoom(Long uid, CursorPageBaseReq request) {
        List<Pair<Long, Double>> roomList = new ArrayList<>();
        // 获取热点群聊
        CursorPageBaseResp<Pair<Long, Double>> hotRoom = hotRoomCache.cursorPage(request);
        roomList.addAll(hotRoom.getData());
        CursorPageBaseResp<Contact> privateRoom = CursorPageBaseResp.empty();
        // 如果用户登陆了，还需要展示用户个人群聊
        if (Objects.nonNull(uid)) {
            privateRoom = contactDao.cursorPageByUid(request, uid);
            List<Pair<Long, Double>> privateRoomIds = privateRoom.getData().stream().map(room -> Pair.of(room.getRoomId(), (double) room.getActiveTime().getTime())).collect(Collectors.toList());
            roomList.addAll(privateRoomIds);
        }
        if (CollectionUtils.isEmpty(roomList)) {
            return CursorPageBaseResp.empty();
        }
        roomList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        List<Long> roomIds = roomList.subList(0, request.getPageSize()).stream().map(Pair::getKey).collect(Collectors.toList());
        List<ChatRoomResp> result = buildChatRoomResp(roomIds, uid);
        // 判断是否最后一页
        // 如果热点或私人群聊其中一个不是最后一页，那么聚合后肯定也不是最后一页
        // 只有当两类群聊都是最后一页，且聚合后数据量小于等于请求的pageSize时，才是最后一页
        // 当只有一类数据时，且这类数量刚好是pageSize+ 1时，只用数量判断的话无法判断
        boolean isLast = hotRoom.getIsLast() && privateRoom.getIsLast() && roomList.size() <= request.getPageSize();
        return new CursorPageBaseResp<>(result.get(result.size()).getActiveTime().getTime() + "", isLast, result);
    }

    @Override
    public List<ChatRoomResp> buildChatRoomResp(List<Long> roomIds, Long uid) {
        // 获取基本信息和群聊头像、名称
        List<ChatRoomDTO> chatRoomDTOList = getBaseChatRoom(roomIds, uid);

        List<Long> msgIdList = chatRoomDTOList.stream().map(ChatRoomDTO::getLastMsgId).collect(Collectors.toList());
        // 获取最后一条消息，并且转换成对应的显示格式
        List<Message> messages = messageDao.listByIds(msgIdList);
        List<Long> senderUidList = messages.stream().map(Message::getFromUid).collect(Collectors.toList());
        Map<Long, User> senderInfoMap = userCache.getUserInfoBatch(senderUidList);
        Map<Long, Message> messageMap = messages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        Map<Long, Contact> contactMap = contactDao.listByUid(uid).stream().collect(Collectors.toMap(Contact::getRoomId, Function.identity()));
        return chatRoomDTOList.stream().map(dto -> {
            ChatRoomResp chatRoomResp = RoomAdapter.buildResp(dto);
            Message message = messageMap.get(dto.getLastMsgId());
            User sender = senderInfoMap.get(message.getFromUid());
            // todo 消息转换器，将消息转换成对应的显示内容

            // 获取群聊的消息未读数
            Contact contact = contactMap.get(dto.getRoomId());
            int count = messageDao.countUnReadMsg(dto.getRoomId(), Optional.of(contact).map(Contact::getReadTime).orElse(null));
            chatRoomResp.setUnreadCount(count);
            return chatRoomResp;
        }).collect(Collectors.toList());
    }

    @Override
    public ChatRoomResp getChatRoomDetail(IdBaseReq req, Long uid) {
        Room room = roomCache.get(req.getId());
        if (Objects.isNull(room)) {
            throw new BusinessException("房间号有误，数据异常");
        }
        return buildChatRoomResp(Collections.singletonList(req.getId()), uid).get(0);
    }

    @Override
    public ChatRoomResp detailChatRoomByTargetId(ContactFriendReq req, Long uid) {
        String roomKey = RoomAdapter.buildRoomKey(req.getUid(), uid);
        RoomFriend roomFriend = roomFriendDao.findByRoomKey(roomKey);
        if (Objects.isNull(roomFriend)) {
            throw new BusinessException("该用户不是你的好友");
        }
        return buildChatRoomResp(Collections.singletonList(roomFriend.getRoomId()), uid).get(0);
    }

    private List<ChatRoomDTO> getBaseChatRoom(List<Long> roomIds, Long uid) {
        Map<Long, Room> roomMap = roomCache.getBatch(roomIds);
        // 将room以类型分组
        Map<Integer, List<Long>> roomTypeMap = roomMap.values()
                .stream().collect(Collectors.groupingBy(Room::getType,
                        Collectors.mapping(Room::getId, Collectors.toList())));
        // 根据单聊房间号获取roomFriend信息
        Map<Long, RoomFriend> singleRoomMap = roomFriendCache.getBatch(
                Optional.ofNullable(roomTypeMap.get(RoomTypeEnum.SINGLE.getCode())).orElse(null));
        // 根据群聊房间号获取roomGroup信息
        Map<Long, RoomGroup> groupRoomMap = roomGroupCache.getBatch(
                Optional.ofNullable(roomTypeMap.get(RoomTypeEnum.GROUP.getCode())).orElse(null));
        // 批量获取好友的信息，单聊聊天室需要用到
        List<Long> friendList = singleRoomMap.values().stream()
                .map(roomFriend -> RoomAdapter.getFriendUid(roomFriend, uid))
                .collect(Collectors.toList());
        Map<Long, User> friendMap = userCache.getUserInfoBatch(friendList);
        return roomIds.stream().map(id -> {
            Room room = roomMap.get(id);
            ChatRoomDTO chatRoomDTO = RoomAdapter.buildDTO(room);
            if (RoomTypeEnum.SINGLE.getCode().equals(room.getType())) {
                // 如果是单聊，那么聊天室的名称和头像应该是好友的名称和头像
                RoomFriend roomFriend = singleRoomMap.get(id);
                Long friendUid = RoomAdapter.getFriendUid(roomFriend, uid);
                User userInfo = friendMap.get(friendUid);
                chatRoomDTO.setName(userInfo.getName());
                chatRoomDTO.setAvatar(userInfo.getAvatar());
            } else if (RoomTypeEnum.GROUP.getCode().equals(room.getType())) {
                RoomGroup roomGroup = groupRoomMap.get(id);
                chatRoomDTO.setName(roomGroup.getName());
                chatRoomDTO.setAvatar(roomGroup.getAvatar());
            }
            return chatRoomDTO;
        }).collect(Collectors.toList());
    }
}
