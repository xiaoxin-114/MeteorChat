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
     * @param roomId 房间id
     * @param uid 登陆用户
     * @return
     */
    public List<Contact> listByRoomId(Long roomId, Long uid) {
        return lambdaQuery().eq(Contact::getRoomId, roomId)
                .ne(Objects.nonNull(uid),  Contact::getUid, uid)
                .list();
    }

    public void removeContact(Long roomId, Long uid) {
        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<Contact>()
                .eq(Contact::getRoomId, roomId)
                .eq(Contact::getUid, uid);
        remove(queryWrapper);
    }

    /**
     * 移除一个聊天室的所有记录
     * @param roomId
     */
    public void removeByRoomId(Long roomId) {
        LambdaQueryWrapper<Contact> queryWrapper = new LambdaQueryWrapper<Contact>()
                .eq(Contact::getRoomId, roomId);
        remove(queryWrapper);
    }

    public void refreshActiveTime(Long roomId, Date sendTime, Long msgId) {
        lambdaUpdate().set(Contact::getLastMsgId, msgId)
                .set(Contact::getActiveTime, sendTime)
                .eq(Contact::getRoomId, roomId)
                .lt(Contact::getLastMsgId, msgId)
                .update();
    }

    /**
     * 根据用户id和房间号获取信箱
     * @param roomId
     * @param uid
     * @return
     */
    public Contact getByUidAndRoomId(Long roomId, Long uid) {
        return lambdaQuery().eq(Contact::getRoomId, roomId)
                .eq(Contact::getUid, uid)
                .one();
    }
}
