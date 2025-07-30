package com.meteor.chat.user.api;

import com.meteor.chat.api.user.UserRoleCommonApi;
import com.meteor.chat.user.dao.UserRoleDao;
import com.meteor.chat.user.domain.entity.UserRole;
import com.meteor.chat.user.enums.RoleEnum;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;
@RestController
public class UserRoleCommonApiImpl implements UserRoleCommonApi {

    @Resource
    private UserRoleDao userRoleDao;

    @Override
    public boolean isSuperAdmin(Long uid) {
        UserRole role = userRoleDao.getUserRoleByUid(uid);
        return Objects.nonNull(role) &&
                (RoleEnum.SUPERADMIN.getId().equals(role.getRoleId())
                        || RoleEnum.CHAT_ADMIN.getId().equals(role.getRoleId()));
    }
}
