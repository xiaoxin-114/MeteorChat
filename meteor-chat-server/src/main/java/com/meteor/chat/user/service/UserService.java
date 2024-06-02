package com.meteor.chat.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meteor.chat.common.domain.dto.ItemInfoDTO;
import com.meteor.chat.common.domain.dto.SummaryInfoDTO;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.vo.BadgeResp;
import com.meteor.chat.common.domain.vo.UserInfoVO;
import com.meteor.chat.common.domain.vo.req.ItemInfoReq;
import com.meteor.chat.common.domain.vo.req.ModifyNameReq;
import com.meteor.chat.common.domain.vo.req.SummaryInfoReq;

import java.util.List;

public interface UserService {
    void register(User user);

    UserInfoVO getUserInfo(Long uid);

    void wearBadge(Long uid, Long itemId);

    /**
     * 判断用户是否有拉黑的权限（系统管理员，群聊管理员都能拉黑）
     * @param uid
     * @return
     */
    boolean isAdmin(Long uid);

    /**
     * 拉黑用户
     * @param blackId
     */
    void black(Long blackId);

    void rename(Long uid, ModifyNameReq req);

    /**
     * 懒加载，批量更新用户的数据
     * @param req
     * @return
     */
    List<SummaryInfoDTO> getSummaryInfoDTOList(SummaryInfoReq req);

    List<ItemInfoDTO> getItemInfoDTOList(ItemInfoReq req);
}
