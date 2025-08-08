package com.meteor.chat.room.service.impl;

import com.meteor.chat.api.msg.MessageCommonApi;
import com.meteor.chat.api.msg.dto.BaseMemberChangDTO;
import com.meteor.chat.api.room.dto.RoomFriendDTO;
import com.meteor.chat.api.room.enums.RoomTypeEnum;
import com.meteor.chat.api.user.UserInfoCommonApi;
import com.meteor.chat.api.user.UserRoleCommonApi;
import com.meteor.chat.api.user.dto.UserCursorPageDTO;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.push.common.core.push.PushService;
import com.meteor.chat.push.common.domain.vo.WSBaseResp;
import com.meteor.chat.redis.core.annotation.RedissonLock;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.room.adapter.WSAdapter;
import com.meteor.chat.room.dao.*;
import com.meteor.chat.room.domain.entity.GroupMember;
import com.meteor.chat.room.domain.entity.Room;
import com.meteor.chat.room.domain.entity.RoomFriend;
import com.meteor.chat.room.domain.entity.RoomGroup;
import com.meteor.chat.room.domain.vo.*;
import com.meteor.chat.room.enums.GroupRoleAPPEnum;
import com.meteor.chat.room.enums.RoomFriendStatusEnum;
import com.meteor.chat.room.event.GroupMemberAddEvent;
import com.meteor.chat.room.service.RoomService;
import com.meteor.chat.room.service.adapter.RoomAdapter;
import com.meteor.chat.room.service.cache.GroupMemberCache;
import com.meteor.chat.room.service.cache.RoomCache;
import com.meteor.chat.room.service.cache.RoomGroupCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Assert;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

    @Resource
    private RoomDao roomDao;

    @Resource
    private RoomGroupDao roomGroupDao;

    @Resource
    private RoomFriendDao roomFriendDao;

    @Resource
    private GroupMemberDao groupMemberDao;

    @Resource
    private RoomCache roomCache;

    @Resource
    private RoomGroupCache roomGroupCache;

    @Resource
    private GroupMemberCache groupMemberCache;

    @DubboReference
    private UserInfoCommonApi userInfoCommonApi;

    @Resource
    private ContactDao contactDao;

    @DubboReference
    private UserRoleCommonApi userRoleCommonApi;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private PushService pushService;

    @DubboReference
    private MessageCommonApi messageCommonApi;

    @Override
    public GroupResp groupDetail(IdBaseReq req, Long uid) {
        Room room = roomCache.get(req.getId());
        RoomGroup roomGroup = roomGroupCache.get(req.getId());
        Set<Long> onlineUidSet = userInfoCommonApi.getOnlineUidSet().getCheckData();
        long onlineCount;
        // 计算群聊在线人数
        if (room.isHotRoom()) {
            onlineCount = onlineUidSet.size();
        }else {
            List<Long> uidList = groupMemberCache.getMemberUidList(req.getId());
            onlineCount = uidList.stream()
                    .filter(onlineUidSet::contains).count();
        }
        GroupRoleAPPEnum groupRole = getGroupRole(uid, room, roomGroup);
        return GroupResp.builder()
                .roomId(room.getId())
                .groupName(roomGroup.getName())
                .avatar(roomGroup.getAvatar())
                .role(groupRole.getCode())
                .onlineNum(onlineCount)
                .build();
    }

    @Override
    public CursorPageBaseResp<GroupMemberResp> cursorPageMember(RoomMemberCursorReq req) {
        Room room = roomCache.get(req.getRoomId());
        RoomGroup roomGroup = roomGroupCache.get(req.getRoomId());
        List<Long> uidList = null;
        if (!room.isHotRoom()) {
            uidList = groupMemberCache.getMemberUidList(req.getRoomId());
        }
        UserCursorPageDTO cursorPageDTO = UserCursorPageDTO.builder()
                .roomId(req.getRoomId())
                .uidList(uidList)
                .build();
        cursorPageDTO.setPageSize(req.getPageSize());
        cursorPageDTO.setCursor(req.getCursor());
        CursorPageBaseResp<UserInfoDTO> userPage = userInfoCommonApi.cursorPageUser(cursorPageDTO).getCheckData();
        if (CollectionUtils.isEmpty(userPage.getList())) {
            return CursorPageBaseResp.empty();
        }
        List<GroupMember> memberList = groupMemberDao.listByGroupIdAndUids(roomGroup.getId(),
                userPage.getList().stream().map(UserInfoDTO::getUid).collect(Collectors.toList()));
        List<GroupMemberResp> groupMemberList = RoomAdapter.buildMemberResp(userPage.getList(), memberList);
        return CursorPageBaseResp.init(userPage, groupMemberList);
    }

    @Override
    public List<GroupMemberListResp> getMemberList(RoomMemberReq req) {
        Long roomId = req.getRoomId();
        Room room = roomCache.get(roomId);
        Assert.assertNotNull("聊天室不存在", room);
        if (room.isHotRoom()) {
             List<UserInfoDTO> userList = userInfoCommonApi.getAllUser().getCheckData();
             return RoomAdapter.buildMemberListResp(userList);
        } else {
            // 同样需要根据情况处理，可能实际数据过大，或者和微信一样，限制群聊用户上限
            List<Long> uidList = groupMemberCache.getMemberUidList(roomId);
            List<UserInfoDTO> userList = userInfoCommonApi.getUserInfoList(uidList).getCheckData();
            return RoomAdapter.buildMemberListResp(userList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(MemberDelReq req, Long uid) {
        Assert.assertNotEquals("不能移除自己", req.getUid(), uid);
        Long roomId = req.getRoomId();
        Room room = roomCache.get(roomId);
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        Assert.assertFalse("全员群不支持移除群成员", room.isHotRoom());
        Assert.assertNotNull("聊天室id错误", roomGroup);
        GroupRoleAPPEnum deleteRole = getGroupRole(req.getUid(), room, roomGroup);
        Assert.assertNotEquals("群主无法被移出群聊", GroupRoleAPPEnum.LEADER, deleteRole);
        GroupRoleAPPEnum userRole = getGroupRole(uid, room, roomGroup);
        if (GroupRoleAPPEnum.MANAGER.equals(deleteRole)) {
            Assert.assertEquals("管理员只能被群主移出群聊", GroupRoleAPPEnum.LEADER, userRole);
        } else if (GroupRoleAPPEnum.MEMBER.equals(deleteRole)) {
            Assert.assertTrue("登陆用户没有权限", hasPower(userRole, uid));
        } else {
            throw new BusinessException("当前用户已不在群聊");
        }
        groupMemberDao.removeMember(roomGroup.getId(), req.getUid());
        contactDao.removeContact(roomId, req.getUid());
        // 向所有用户端推送用户被移除的消息
        List<Long> uidList = groupMemberCache.getMemberUidList(roomId);
        WSBaseResp<WSMemberChange> wsBaseResp = WSAdapter.buildGroupMemberRemove(roomId, req.getUid());
        pushService.pushRoomMsg(wsBaseResp, uidList);
        groupMemberCache.evictMemberUidList(roomId);
        messageCommonApi.sendMemberRemovedMsg(BaseMemberChangDTO.builder().roomId(roomId).uid(req.getUid()).build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exitRoom(MemberExitReq req, Long uid) {
        Long roomId = req.getRoomId();
        Room room = roomCache.get(roomId);
        Assert.assertNotNull("聊天室id错误", room);
        Assert.assertFalse("全员群不允许退出", room.isHotRoom());
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        Assert.assertNotNull("聊天室id错误", roomGroup);
        GroupRoleAPPEnum groupRole = getGroupRole(uid, room, roomGroup);
        Assert.assertNotEquals("当前用户不在群聊内", GroupRoleAPPEnum.REMOVE, groupRole);
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
        if (GroupRoleAPPEnum.LEADER.equals(groupRole) || memberUidList.size() - 1 < 2) {
            // 如果是群主就直接解散群聊，或者离开后群聊只剩群主一人
            groupMemberDao.removeMember(roomGroup.getId(), null);
            contactDao.removeByRoomId(roomId);
            groupMemberCache.evictMemberUidList(roomId);
            roomGroupDao.removeById(roomGroup.getId());
            roomDao.removeById(roomId);
            // todo 向所有群成员推送群已经被解散的消息

            // 删除群聊的消息记录
            messageCommonApi.removeRoomMsg(roomId);
        } else {
            groupMemberDao.removeMember(roomGroup.getId(), uid);
            contactDao.removeContact(roomId, uid);
            groupMemberCache.evictMemberUidList(roomId);
            messageCommonApi.sendMemberExitMsg(BaseMemberChangDTO.builder().roomId(roomId).uid(uid).build());
            // 向所有成员推送用户退出群聊的消息
            pushService.pushRoomMsg(WSAdapter.buildGroupMemberRemove(roomId, uid), memberUidList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "'createGroup:'+#uid")
    public Long createChatGroup(GroupAddReq req, Long uid) {
        if (Objects.isNull(uid)) {
            throw new BusinessException("用户未登陆，创建群聊失败");
        }
        int count = groupMemberDao.countLeader(uid);
        Assert.assertTrue("该用户已经创建过群聊", count < 1);
        RoomGroup roomGroup = buildGroupRoom(uid);
        // 用户的群主角色
        GroupMember leaderMember = RoomAdapter.buildGroupMember(uid,  roomGroup, GroupRoleAPPEnum.LEADER);
        // 邀请好友的普通成员角色
        List<GroupMember> groupMembers = req.getUidList().stream().map(id -> RoomAdapter.buildGroupMember(id, roomGroup, GroupRoleAPPEnum.MEMBER))
                .collect(Collectors.toList());
        groupMembers.add(leaderMember);
        groupMemberDao.saveBatch(groupMembers);
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, groupMembers, roomGroup, uid));
        return roomGroup.getRoomId();
    }

    @Override
    public void addGroupMembers(MemberAddReq req, Long uid) {
        Long roomId = req.getRoomId();
        Room room = roomCache.get(roomId);
        Assert.assertNotNull("聊天室id异常", room);
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        Assert.assertNotNull("聊天室id异常", roomGroup);
        Assert.assertFalse("全员群不需要邀请成员", room.isHotRoom());
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
        List<GroupMember> needAddGroupMember = req.getUidList().stream()
                .filter(id -> !memberUidList.contains(id))
                .map(id -> RoomAdapter.buildGroupMember(id, roomGroup, GroupRoleAPPEnum.MEMBER))
                .collect(Collectors.toList());
        groupMemberDao.saveBatch(needAddGroupMember);
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, needAddGroupMember, roomGroup, uid));
    }

    @Override
    public void addAdmin(AdminChangeReq req, Long uid) {
        Long roomId = req.getRoomId();
        Room room = roomCache.get(roomId);
        Assert.assertNotNull("房间号id异常", room);
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        Assert.assertNotNull("房间号id异常", roomGroup);
        Assert.assertNotNull("用户未登陆", uid);
        GroupRoleAPPEnum groupRole = getGroupRole(uid, room, roomGroup);
        Assert.assertEquals("只有群主才能添加管理员", GroupRoleAPPEnum.LEADER, groupRole);
        Map<Long, GroupMember> memberMap = groupMemberCache.getMemberList(roomId);
        List<Long> uidList = req.getUidList();
        Assert.assertTrue("请确保所有用户都在群聊内", memberMap.keySet().containsAll(uidList));
        uidList = uidList.stream()
                .filter(id -> memberMap.get(id).getRole().equals(GroupRoleAPPEnum.MEMBER.getCode()))
                .collect(Collectors.toList());
        long managerCount = memberMap.values().stream()
                .filter(groupMember -> GroupRoleAPPEnum.MANAGER.getCode().equals(groupMember.getRole()))
                .count();
        Assert.assertTrue("群聊管理员不能超过" + CommonConstants.MAX_ADMIN_NUM + "个",
                uidList.size() + managerCount <= CommonConstants.MAX_ADMIN_NUM);
        if (!CollectionUtils.isEmpty(uidList)) {
            groupMemberDao.addAdmin(uidList, roomGroup.getId());
        }
    }

    @Override
    public void removeAdmin(AdminChangeReq req, Long uid) {
        Long roomId = req.getRoomId();
        Room room = roomCache.get(roomId);
        Assert.assertNotNull("房间号id异常", room);
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        Assert.assertNotNull("房间号id异常", roomGroup);
        Assert.assertNotNull("用户未登陆", uid);
        GroupRoleAPPEnum groupRole = getGroupRole(uid, room, roomGroup);
        Assert.assertEquals("只有群主才能移除管理员", GroupRoleAPPEnum.LEADER, groupRole);
        Map<Long, GroupMember> memberMap = groupMemberCache.getMemberList(roomId);
        List<Long> uidList = req.getUidList();
        Assert.assertTrue("请确保所有用户都在群聊内", memberMap.keySet().containsAll(uidList));
        groupMemberDao.removeAdmin(uidList, roomGroup.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long buildSingleRoom(Long uid1, Long uid2) {
        if (uid1 == null || uid2 == null) {
            throw new BusinessException("用户id为空，创建单聊失败");
        }
        String roomKey = RoomAdapter.buildRoomKey(uid1, uid2);
        RoomFriend roomFriend = roomFriendDao.findByRoomKey(roomKey);
        if (Objects.nonNull(roomFriend)) {
            restoreRoomFriend(roomFriend);
            return roomFriend.getRoomId();
        } else {
            Room room = RoomAdapter.buildRoom(RoomTypeEnum.SINGLE);
            roomDao.save(room);
            RoomFriend newRoomFriend = RoomAdapter.buildFriendRoom(uid1, uid2, room.getId());
            roomFriendDao.save(newRoomFriend);
            return room.getId();
        }
    }

    /**
     * 恢复聊天室，如果聊天室被禁用了，恢复正常使用
     */
    private void restoreRoomFriend(RoomFriend roomFriend) {
        if (RoomFriendStatusEnum.FORBID.getCode().equals(roomFriend.getStatus())) {
            roomFriendDao.updateRoomStatus(roomFriend.getId(), RoomFriendStatusEnum.NORAML);
        }
    }

    /**
     * 获取用户在群组中的权限
     * @param uid 用户id
     * @param room 聊天室
     * @param roomGroup 群聊信息
     */
    private GroupRoleAPPEnum getGroupRole(Long uid, Room room, RoomGroup roomGroup) {
        // 有可能是未登陆用户，查看全员的热点群
        GroupMember member = Objects.nonNull(uid) ? groupMemberDao.getByUidAndGroupId(uid, roomGroup.getId()) : null;
        if (Objects.nonNull(member)) {
            return GroupRoleAPPEnum.of(member.getRole());
        }else {
            // 用户未登录，热点群就是普成员，
            if (room.isHotRoom()) {
                return GroupRoleAPPEnum.MEMBER;
            }else {
                // 如果登陆了，还没数据，说明被移出群聊了，是被移除的成员
                return GroupRoleAPPEnum.REMOVE;
            }
        }
    }

    /**
     * 判断用户是否有群聊管理员权限
     * 是系统管理员或者群聊管理员
     */
    private boolean hasPower(GroupRoleAPPEnum groupRole, Long uid) {
        boolean power = userRoleCommonApi.isSuperAdmin(uid).getCheckData();
        return power || GroupRoleAPPEnum.LEADER.equals(groupRole) || GroupRoleAPPEnum.MANAGER.equals(groupRole);
    }

    /**
     * 判断用户在群聊中是否有管理权限
     * @param uid 用户id
     * @param roomId 群聊id
     */
    @Override
    public boolean hasRoomPower(Long uid, Long roomId) {
        Room room = roomCache.get(roomId);
        Assert.assertNotNull("房间号有误", room);
        boolean systemAdmin = userRoleCommonApi.isSuperAdmin(uid).getCheckData();
        if (room.isHotRoom()) {
            // 如果是热门群聊，取决于用户是否是系统管理员
            return systemAdmin;
        } else {
            if (systemAdmin) {
                // 系统管理员同样用于其他群聊的管理权限，但是没有群主权限
                return true;
            } else {
                Map<Long, GroupMember> groupMemberMap = groupMemberCache.getMemberList(roomId);
                Assert.assertTrue("群聊数据异常", groupMemberMap != null && !groupMemberMap.isEmpty());
                GroupMember groupMember = groupMemberMap.get(uid);
                Assert.assertNotNull("用户不在群聊", groupMember);
                Integer role = groupMember.getRole();
                return GroupRoleAPPEnum.LEADER.getCode().equals(role) || GroupRoleAPPEnum.MANAGER.getCode().equals(role);
            }
        }
    }

    @Override
    public void disableSingleRoom(Long uid1, Long uid2) {
        roomFriendDao.disableRoomByKey(RoomAdapter.buildRoomKey(uid1, uid2));
    }

    @Override
    public RoomFriendDTO getRoomFriendInfo(Long roomId) {
        RoomFriend roomFriend = roomFriendDao.getByRoomId(roomId);
        if (roomFriend == null) {
            return null;
        }
        return RoomFriendDTO.builder().roomId(roomFriend.getRoomId())
                .uid1(roomFriend.getUid1())
                .uid2(roomFriend.getUid2()).build();
    }

    /**
     * 创建群聊
     * @param uid 创建人id
     * @return 群聊room
     */
    private RoomGroup buildGroupRoom(Long uid) {
        Room room = RoomAdapter.buildRoom(RoomTypeEnum.GROUP);
        roomDao.save(room);
        UserInfoDTO user = userInfoCommonApi.getUserInfo(uid).getCheckData();
        RoomGroup roomGroup = RoomAdapter.buildRoomGroup(user, room);
        roomGroupDao.save(roomGroup);
        return roomGroup;
    }
}
