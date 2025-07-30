package com.meteor.chat.user.service;


import com.meteor.chat.user.domain.vo.IdRespVO;
import com.meteor.chat.user.domain.vo.UserEmojiResp;
import com.meteor.chat.user.domain.vo.req.IdBaseReq;
import com.meteor.chat.user.domain.vo.req.UserEmojiAddReq;

import java.util.List;

public interface UserEmojiService {
    List<UserEmojiResp> listEmoji(Long uid);

    IdRespVO addEmoji(UserEmojiAddReq req, Long uid);

    void removeEmoji(IdBaseReq req, Long uid);
}
