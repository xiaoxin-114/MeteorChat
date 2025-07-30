package com.meteor.chat.room.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.mybatis.domain.CursorPageBaseReq;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.redis.core.util.CursorUtils;
import com.meteor.chat.room.domain.entity.Contact;
import com.meteor.chat.room.mapper.ContactMapper;
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

    public CursorPageBaseResp<Contact> cursorReadPage(CursorPageBaseReq req, Long roomId, Date createTime) {
        return CursorUtils.cursorPage(req, this
                , (lambdaQuery) -> lambdaQuery.eq(Contact::getRoomId, roomId).ge(Contact::getReadTime, createTime)
                , Contact::getReadTime);
    }

    public CursorPageBaseResp<Contact> cursorUnReadPage(CursorPageBaseReq req, Long roomId, Date createTime) {
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

    /**
     * 更新contact表格的最新消息id和活跃时间，如果数据库中没有记录则新增，有则修改
     * @param roomId
     * @param uidList
     * @param sendTime
     * @param msgId
     */
    public void refreshActiveTime(Long roomId, List<Long> uidList, Date sendTime, Long msgId) {
        super.getBaseMapper().refreshActiveTime(roomId, uidList, sendTime, msgId);
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
