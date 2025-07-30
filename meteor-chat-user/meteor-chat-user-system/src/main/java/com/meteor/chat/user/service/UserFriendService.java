package com.meteor.chat.user.service;

import com.meteor.chat.mybatis.domain.CursorPageBaseReq;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.user.domain.vo.*;
import com.meteor.chat.user.domain.vo.req.FriendApproveReq;
import com.meteor.chat.user.domain.vo.req.FriendCheckReq;
import com.meteor.chat.user.domain.vo.req.FriendDeleteReq;
import com.meteor.chat.user.domain.vo.req.PageBaseReq;

public interface UserFriendService {


    /**
     * 用户的联系人列表游标分页
     * @param uid 用户id
     * @param request 游标请求
     * @return
     */
    CursorPageBaseResp<FriendResp> pageFriendList(Long uid, CursorPageBaseReq request);

    /**
     * 发起好友申请
     * @param uid 用户id
     * @param targetUid 申请好友的用户id
     */
    void applyUser(Long uid, Long targetUid, String msg);

    /**
     * 获取未读好友申请的数量
     * @return
     */
    FriendUnreadResp countUnread(Long uid);

    FriendCheckResp batchCheckFriends(Long uid, FriendCheckReq req);

    PageBaseResp<FriendApplyResp> applyPage(PageBaseReq req, Long uid);

    void processApply(FriendApproveReq req);

    void deleteFriend(FriendDeleteReq req, Long uid);
}