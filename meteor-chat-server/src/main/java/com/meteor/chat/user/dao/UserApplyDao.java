package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.UserApply;
import com.meteor.chat.common.domain.enums.ReadEnum;
import com.meteor.chat.common.domain.enums.UserApplyStatusEnum;
import com.meteor.chat.common.domain.enums.UserApplyTypeEnum;
import com.meteor.chat.common.mapper.UserApplyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply> {

    public UserApply getWaitingApply(Long uid, Long targetId) {
        LambdaQueryWrapper<UserApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserApply::getUid, uid)
                .eq(UserApply::getTargetId, targetId)
                .eq(UserApply::getStatus, UserApplyStatusEnum.WAITING.getCode());
        return getOne(queryWrapper);
    }

    public int countUnRead(Long uid) {
        return count(
                new LambdaQueryWrapper<UserApply>()
                        .eq(UserApply::getTargetId, uid)
                        .eq(UserApply::getStatus, UserApplyStatusEnum.WAITING.getCode()));
    }

    /**
     * 分页查询好友申请列表
     * @param plusPage
     * @param uid
     * @return
     */
    public Page<UserApply> getApplyPage(Page plusPage, Long uid) {
        return page(plusPage,
                new LambdaQueryWrapper<UserApply>()
                        .eq(UserApply::getTargetId, uid)
                        .eq(UserApply::getType, UserApplyTypeEnum.GETFRIEND.getCode())
                        .orderByDesc(UserApply::getCreateTime));
    }

    public void readApply(List<Long> ids) {
        lambdaUpdate()
                .in(UserApply::getId, ids)
                .set(UserApply::getReadStatus, ReadEnum.READED.getCode())
                .update();
    }

    public void approveApply(Long applyId) {
        lambdaUpdate()
                .eq(UserApply::getId, applyId)
                .set(UserApply::getStatus, UserApplyStatusEnum.PERMITTED.getCode())
                .update();
    }
}
