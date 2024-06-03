package com.meteor.chat.chat.service;

import com.meteor.chat.common.domain.vo.ChatRoomResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.ContactFriendReq;
import com.meteor.chat.common.domain.vo.req.CursorPageBaseReq;
import com.meteor.chat.common.domain.vo.req.IdBaseReq;

import java.util.List;

public interface ContactService {

    /**
     * 游标分页查询用户的会话列表
     * @param uid 用户id
     * @param request 页面大小和游标等信息
     * @return
     */
    CursorPageBaseResp<ChatRoomResp> pageChatRoom(Long uid, CursorPageBaseReq request);

    /**
     * 通过roomid来封装给前端的数据格式，
     * 因为roomid已经排序了，所以方法中不会再排序，而是按照roomid的顺序返回
     * @param roomIds 房间id，并且已经按照最后活跃时间倒序排序
     * @param uid 用户id
     * @return
     */
    List<ChatRoomResp> buildChatRoomResp(List<Long> roomIds, Long uid);

    /**
     * 获取聊天室详情
     * @param req
     * @return
     */
    ChatRoomResp getChatRoomDetail(IdBaseReq req, Long uid);

    /**
     * 根据对象id获取聊天室详情
     * @param req
     * @return
     */
    ChatRoomResp detailChatRoomByTargetId(ContactFriendReq req, Long uid);
}

