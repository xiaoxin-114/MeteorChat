package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meteor.chat.common.domain.entity.UserRole;

public interface UserRoleDao extends IService<UserRole> {

    UserRole getUserRoleByUid(Long uid);
}
