package com.meteor.chat.msg.service;

import com.meteor.chat.common.domain.vo.ChatRoomResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.CursorPageBaseReq;

public interface RoomService {
    /**
     * 游标分页查询用户的会话列表
     * @param uid 用户id
     * @param request 页面大小和游标等信息
     * @return
     */
    CursorPageBaseResp<ChatRoomResp> pageChatRoom(Long uid, CursorPageBaseReq request);

    /**
     * 创建单聊会话
     * @param uid1 用户1的uid
     * @param uid2 用户2的uid
     * @return 会话id
     */
    Long buildSingleRoom(Long uid1, Long uid2);
}
