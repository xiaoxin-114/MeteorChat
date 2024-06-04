package com.meteor.chat.chat.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.Contact;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.enums.HotFlagEunm;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.CursorPageBaseReq;
import com.meteor.chat.common.domain.vo.req.MessageReadCursorPageReq;
import com.meteor.chat.common.mapper.ContactMapper;
import com.meteor.chat.common.mapper.RoomMapper;
import com.meteor.chat.common.util.CursorUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {

    /**
     * 游标分页获取用户的私人会话列表
     * @param req 游标分页请求
     * @param uid 用户id
     * @return
     */
    public CursorPageBaseResp<Contact> cursorPageByUid(CursorPageBaseReq req, Long uid) {
        return CursorUtils.cursorPage(req, this,
                (wrapper) -> wrapper.eq(Contact::getUid, uid),
                Contact::getActiveTime);
    }

    public List<Contact> listByUid(Long uid) {
        return lambdaQuery().eq(Contact::getUid, uid).list();
    }

    public CursorPageBaseResp<Contact> cursorReadPage(MessageReadCursorPageReq req, Long roomId, Date createTime) {
        return CursorUtils.cursorPage(req, this
                , (lambdaQuery) -> lambdaQuery.eq(Contact::getRoomId, roomId).ge(Contact::getReadTime, createTime)
                , Contact::getReadTime);
    }

    public CursorPageBaseResp<Contact> cursorUnReadPage(MessageReadCursorPageReq req, Long roomId, Date createTime) {
        return CursorUtils.cursorPage(req, this
                , (lambdaQuery) -> lambdaQuery.eq(Contact::getRoomId, roomId).lt(Contact::getReadTime, createTime)
                , Contact::getReadTime);
    }

    /**
     * 用于查询消息的已读未读数，查询群聊列表的所有用户记录，除去uid
     * @param roomIds 房间列表
     * @param uid 登陆用户
     * @return
     */
    public List<Contact> listByRoomId(List<Long> roomIds, Long uid) {
        return lambdaQuery().in(Contact::getRoomId, roomIds)
                .ne(Objects.nonNull(uid),  Contact::getUid, uid)
                .list();
    }
}
