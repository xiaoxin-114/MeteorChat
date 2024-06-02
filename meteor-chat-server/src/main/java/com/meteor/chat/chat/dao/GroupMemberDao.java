package com.meteor.chat.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.GroupMember;
import com.meteor.chat.common.mapper.GroupMemberMapper;
import org.springframework.stereotype.Repository;

@Repository
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {
}
