package com.meteor.chat.room.service.impl;

import cn.hutool.core.lang.Pair;
import com.meteor.chat.api.msg.MessageCommonApi;
import com.meteor.chat.api.msg.dto.RoomMsgDTO;
import com.meteor.chat.api.msg.dto.RoomMsgReqDTO;
import com.meteor.chat.api.room.dto.ContactInfoDTO;
import com.meteor.chat.api.room.dto.MessageReadCursorPageDTO;
import com.meteor.chat.api.room.enums.RoomTypeEnum;
import com.meteor.chat.api.user.UserInfoCommonApi;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.redis.core.annotation.RedissonLock;
import com.meteor.chat.mybatis.domain.CursorPageBaseReq;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.room.dao.ContactDao;
import com.meteor.chat.room.dao.RoomFriendDao;
import com.meteor.chat.room.domain.dto.ChatRoomDTO;
import com.meteor.chat.room.domain.entity.Contact;
import com.meteor.chat.room.domain.entity.Room;
import com.meteor.chat.room.domain.entity.RoomFriend;
import com.meteor.chat.room.domain.entity.RoomGroup;
import com.meteor.chat.room.domain.vo.ChatRoomResp;
import com.meteor.chat.room.domain.vo.ContactFriendReq;
import com.meteor.chat.room.domain.vo.IdBaseReq;
import com.meteor.chat.room.service.ContactService;
import com.meteor.chat.room.service.adapter.RoomAdapter;
import com.meteor.chat.room.service.cache.HotRoomCache;
import com.meteor.chat.room.service.cache.RoomCache;
import com.meteor.chat.room.service.cache.RoomFriendCache;
import com.meteor.chat.room.service.cache.RoomGroupCache;
import org.junit.Assert;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    private MessageCommonApi messageCommonApi;

    @Resource
    private RoomFriendDao roomFriendDao;

    @Resource
    private UserInfoCommonApi userInfoCommonApi;

    @Override
    public CursorPageBaseResp<ChatRoomResp> pageChatRoom(Long uid, CursorPageBaseReq request) {
        List<Pair<Long, Double>> roomList = new ArrayList<>();
        // 获取热点群聊
        CursorPageBaseResp<Pair<Long, Double>> hotRoom = hotRoomCache.cursorPage(request);
        roomList.addAll(hotRoom.getList());
        CursorPageBaseResp<Contact> privateRoom = CursorPageBaseResp.empty();
        // 如果用户登陆了，还需要展示用户个人群聊
        if (Objects.nonNull(uid)) {
            privateRoom = contactDao.cursorPageByUid(request, uid);
            // 过滤掉activeTime为空的聊天室，这些聊天室都是热点聊天室
            List<Pair<Long, Double>> privateRoomIds = privateRoom.getList().stream().filter(room -> Objects.nonNull(room.getActiveTime())).map(room -> Pair.of(room.getRoomId(), (double) room.getActiveTime().getTime())).collect(Collectors.toList());
            roomList.addAll(privateRoomIds);
        }
        if (CollectionUtils.isEmpty(roomList)) {
            return CursorPageBaseResp.empty();
        }
        // 将两个房间列表合并后，根据最后活跃时间排序
        roomList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        // 判断是否最后一页
        // 如果热点或私人群聊其中一个不是最后一页，那么聚合后肯定也不是最后一页
        // 只有当两类群聊都是最后一页，且聚合后数据量小于等于请求的pageSize时，才是最后一页
        // 当只有一类数据时，且这类数量刚好是pageSize+ 1时，只用数量判断的话无法判断
        boolean isLast = hotRoom.getIsLast() && privateRoom.getIsLast() && roomList.size() <= request.getPageSize();
        List<Long> roomIds = roomList.stream().map(Pair::getKey).collect(Collectors.toList());
        roomIds = isLast ? roomIds : roomIds.subList(0, request.getPageSize());
        List<ChatRoomResp> result = buildChatRoomResp(roomIds, uid);
        return new CursorPageBaseResp<>(result.get(result.size() - 1).getActiveTime().getTime() + "", isLast, result);
    }

    @Override
    public List<ChatRoomResp> buildChatRoomResp(List<Long> roomIds, Long uid) {
        // 获取基本信息和群聊头像、名称
        List<ChatRoomDTO> chatRoomDTOList = getBaseChatRoom(roomIds, uid);
        Map<Long, Contact> contactMap = contactDao.listByUid(uid).stream().collect(Collectors.toMap(Contact::getRoomId, Function.identity()));

        List<RoomMsgReqDTO> roomMsgReqDTOList = chatRoomDTOList.stream().map(dto -> {
            Contact contact = contactMap.get(dto.getRoomId());
            return RoomMsgReqDTO.builder().roomId(dto.getRoomId())
                    .lastMsgId(dto.getLastMsgId())
                    .readTime(Optional.ofNullable(contact).map(Contact::getReadTime).orElse(null))
                    .build();
        }).collect(Collectors.toList());
        // 获取最后一条消息，并且转换成对应的显示格式
        List<RoomMsgDTO> roomMsgList = messageCommonApi.getRoomMsgList(roomMsgReqDTOList);
        if (Objects.isNull(roomMsgList)) {
            roomMsgList = new ArrayList<>();
        }
        Map<Long, RoomMsgDTO> messageMap = roomMsgList.stream().collect(Collectors.toMap(RoomMsgDTO::getRoomId, Function.identity()));
        return chatRoomDTOList.stream().map(dto -> {
            ChatRoomResp chatRoomResp = RoomAdapter.buildResp(dto);
            RoomMsgDTO roomMsgDTO = messageMap.get(dto.getRoomId());
            if (Objects.nonNull(roomMsgDTO)) {
                chatRoomResp.setText(roomMsgDTO.getMsgText());
                chatRoomResp.setUnreadCount(roomMsgDTO.getUnreadCount());
            }
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

    @Override
    @RedissonLock(key = "#uid")
    public void readMsg(Long roomId, Long uid) {
        Room room = roomCache.get(roomId);
        Assert.assertNotNull("房间号异常", room);
        Contact contact = contactDao.getByUidAndRoomId(roomId, uid);
        if (Objects.isNull(contact)) {
            Contact insert = new Contact();
            insert.setUid(uid);
            insert.setRoomId(roomId);
            insert.setReadTime(new Date());
            // 其余消息在群聊发送消息后自然会更新
            contactDao.save(insert);
        } else {
            Contact update = new Contact();
            update.setId(contact.getId());
            update.setReadTime(new Date());
            contactDao.updateById(update);
        }
    }

    @Override
    public List<ContactInfoDTO> listContactInfoByRoomId(Long roomId, Long uid) {
        List<Contact> contacts = contactDao.listByRoomId(roomId, uid);
        if (!CollectionUtils.isEmpty(contacts)) {
            return contacts.stream().map(contact -> ContactInfoDTO.builder().uid(contact.getUid())
                    .roomId(contact.getRoomId()).lastMsgId(contact.getLastMsgId())
                    .readTime(contact.getReadTime()).build()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public ContactInfoDTO getContactInfoByRoomIdUid(Long roomId, Long uid) {
        Contact contact = contactDao.getByUidAndRoomId(roomId, uid);
        if (Objects.nonNull(contact)) {
            return ContactInfoDTO.builder().uid(contact.getUid()).roomId(contact.getRoomId())
                    .readTime(contact.getReadTime()).build();
        }
        return null;
    }

    @Override
    public CursorPageBaseResp<ContactInfoDTO> cursorMsgReadOrUnReadPage(MessageReadCursorPageDTO req, boolean read) {
        CursorPageBaseResp<Contact> cursorPage = null;
        if (read) {
            cursorPage = contactDao.cursorReadPage(new CursorPageBaseReq(req.getPageSize(), req.getCursor()), req.getRoomId(), req.getMsgCreateTime());
        } else {
            cursorPage = contactDao.cursorUnReadPage(new CursorPageBaseReq(req.getPageSize(), req.getCursor()), req.getRoomId(), req.getMsgCreateTime());
        }
        if (Objects.isNull(cursorPage) || cursorPage.isEmpty()) {
            return null;
        }
        List<ContactInfoDTO> dataList = cursorPage.getList().stream().filter(Objects::nonNull).map(contact ->
                ContactInfoDTO.builder().uid(contact.getUid())
                        .roomId(contact.getRoomId()).lastMsgId(contact.getLastMsgId())
                        .readTime(contact.getReadTime()).build()).collect(Collectors.toList());
        return CursorPageBaseResp.init(cursorPage, dataList);
    }

    private List<ChatRoomDTO> getBaseChatRoom(List<Long> roomIds, Long uid) {
        Map<Long, Room> roomMap = roomCache.getBatch(roomIds);
        // 将room以类型分组
        Map<Integer, List<Long>> roomTypeMap = roomMap.values()
                .stream().collect(Collectors.groupingBy(Room::getType,
                        Collectors.mapping(Room::getId, Collectors.toList())));
        // 根据单聊房间号获取roomFriend信息
        Map<Long, RoomFriend> singleRoomMap = roomFriendCache.getBatch(
                roomTypeMap.get(RoomTypeEnum.SINGLE.getCode()));
        // 根据群聊房间号获取roomGroup信息
        Map<Long, RoomGroup> groupRoomMap = roomGroupCache.getBatch(
                roomTypeMap.get(RoomTypeEnum.GROUP.getCode()));
        // 批量获取好友的信息，单聊聊天室需要用到
        List<Long> friendList = singleRoomMap.values().stream()
                .map(roomFriend -> RoomAdapter.getFriendUid(roomFriend, uid))
                .collect(Collectors.toList());
        Map<Long, UserInfoDTO> friendMap = userInfoCommonApi.getUserInfoMap(friendList);
        return roomIds.stream().map(id -> {
            Room room = roomMap.get(id);
            ChatRoomDTO chatRoomDTO = RoomAdapter.buildDTO(room);
            if (RoomTypeEnum.SINGLE.getCode().equals(room.getType())) {
                // 如果是单聊，那么聊天室的名称和头像应该是好友的名称和头像
                RoomFriend roomFriend = singleRoomMap.get(id);
                Long friendUid = RoomAdapter.getFriendUid(roomFriend, uid);
                UserInfoDTO userInfo = friendMap.get(friendUid);
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
