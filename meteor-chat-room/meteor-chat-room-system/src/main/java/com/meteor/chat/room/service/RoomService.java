package com.meteor.chat.room.service;

import com.meteor.chat.api.room.dto.RoomFriendDTO;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.room.domain.vo.*;

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
    CursorPageBaseResp<GroupMemberResp> cursorPageMember(RoomMemberCursorReq req);

    /**
     * 为@成员，获取群组用户列表
     * @param req 包含会话id
     * @return
     */
    List<GroupMemberListResp> getMemberList(RoomMemberReq req);

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
    void exitRoom(MemberExitReq req, Long uid);

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
     */
    void removeAdmin(AdminChangeReq req, Long uid);

    /**
     * 判断用户是否有聊天室权限，
     * @return 有系统管理员权限或者聊天室管理员权限 返回true
     */
    boolean hasRoomPower(Long uid, Long roomId);

    /**
     * 移除单聊会话
     */
    void disableSingleRoom(Long uid1, Long uid2);

    /**
     * 获取单聊会话信息
     */
    RoomFriendDTO getRoomFriendInfo(Long roomId);
}
