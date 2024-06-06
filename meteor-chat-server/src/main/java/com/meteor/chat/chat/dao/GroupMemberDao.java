package com.meteor.chat.chat.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.GroupMember;
import com.meteor.chat.common.domain.entity.RoomGroup;
import com.meteor.chat.common.domain.enums.GroupRoleAPPEnum;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.GroupMemberResp;
import com.meteor.chat.common.domain.vo.req.MemberCursorReq;
import com.meteor.chat.common.mapper.GroupMemberMapper;
import com.meteor.chat.common.util.CursorUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {
    public List<GroupMember> listByGroupId(Long groupId) {
        return lambdaQuery().eq(GroupMember::getGroupId, groupId).list();
    }

    public GroupMember getByUidAndGroupId(Long uid, Long groupId) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid).one();
    }

    public List<GroupMember> listByGroupIdAndUids(Long groupId, List<Long> uidList) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .list();
    }

    public List<Long> getMemberUidList(Long groupId) {
        List<GroupMember> list = lambdaQuery().eq(GroupMember::getGroupId, groupId)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    /**
     * 将成员移出群聊
     * @param groupId 群聊groupId
     * @param uid 用户id，如果为空，则是将所有人都移出群聊
     */
    public void removeMember(Long groupId, Long uid) {
        LambdaQueryWrapper<GroupMember> queryWrapper = new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, groupId)
                .eq(Objects.nonNull(uid), GroupMember::getUid, uid);
        remove(queryWrapper);
    }

    /**
     * 计算用户为群主的群聊数量
     * @param uid 用户id
     * @return
     */
    public int countLeader(Long uid) {
        return lambdaQuery().eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, GroupRoleAPPEnum.LEADER.getCode())
                .count();
    }

    public List<GroupMember> getMemberList(Long groupId) {
        return lambdaQuery().eq(GroupMember::getGroupId, groupId)
                .list();
    }

    public void addAdmin(List<Long> uidList, Long groupId) {
        lambdaUpdate().eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .set(GroupMember::getRole, GroupRoleAPPEnum.MANAGER.getCode())
                .update();
    }

    public void removeAdmin(List<Long> uidList, Long groupId) {
        lambdaUpdate().eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .set(GroupMember::getRole, GroupRoleAPPEnum.MEMBER.getCode())
                .update();
    }
}
