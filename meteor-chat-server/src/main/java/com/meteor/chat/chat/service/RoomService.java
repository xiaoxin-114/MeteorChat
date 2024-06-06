package com.meteor.chat.chat.service;

import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.GroupMemberListResp;
import com.meteor.chat.common.domain.vo.GroupMemberResp;
import com.meteor.chat.common.domain.vo.GroupResp;
import com.meteor.chat.common.domain.vo.req.*;

import java.util.List;

public interface RoomService {

    /**
     * 创建单聊会话
     * @param uid1 用户1的uid
     * @param uid2 用户2的uid
     * @return 会话id
     */
    Long buildSingleRoom(Long uid1, Long uid2);

    /**
     * 获取群聊详情
     * @param req
     * @param uid
     * @return
     */
    GroupResp groupDetail(IdBaseReq req, Long uid);

    /**
     * 游标分页查询群成员
     * @param req
     * @return
     */
    CursorPageBaseResp<GroupMemberResp> cursorPageMember(MemberCursorReq req);

    /**
     * 为@成员，获取群组用户列表
     * @param req 包含会话id
     * @return
     */
    List<GroupMemberListResp> getMemberList(IdBaseReq req);

    /**
     * 移除群成员
     * @param req
     * @param uid
     */
    void removeMember(MemberDelReq req, Long uid);

    /**
     * 主动退出群聊
     * @param req
     * @param uid
     */
    void exitRoom(IdBaseReq req, Long uid);

    /**
     * 用户创建群聊
     * @param req
     * @param uid
     */
    Long createChatGroup(GroupAddReq req, Long uid);

    /**
     * 邀请好友
     * @param req
     * @param uid 用户id
     */
    void addGroupMembers(MemberAddReq req, Long uid);

    /**
     * 添加管理员
     * @param req
     * @param uid
     */
    void addAdmin(AdminChangeReq req, Long uid);

    /**
     * 移除管理员
     * @param req
     * @param uid
     */
    void removeAdmin(AdminChangeReq req, Long uid);
}
