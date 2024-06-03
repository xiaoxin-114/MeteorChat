package com.meteor.chat.chat.service;

import com.meteor.chat.common.domain.vo.ChatRoomResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.CursorPageBaseReq;

public interface RoomService {

    /**
     * 创建单聊会话
     * @param uid1 用户1的uid
     * @param uid2 用户2的uid
     * @return 会话id
     */
    Long buildSingleRoom(Long uid1, Long uid2);
}
