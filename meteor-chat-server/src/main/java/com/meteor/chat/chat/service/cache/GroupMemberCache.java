package com.meteor.chat.chat.service.cache;

import com.meteor.chat.chat.dao.GroupMemberDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class GroupMemberCache {

    @Resource
    private GroupMemberDao groupMemberDao;


}
