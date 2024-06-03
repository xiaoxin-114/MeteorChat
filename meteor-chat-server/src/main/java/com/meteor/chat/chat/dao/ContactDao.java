package com.meteor.chat.chat.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.Contact;
import com.meteor.chat.common.domain.entity.Room;
import com.meteor.chat.common.domain.enums.HotFlagEunm;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.CursorPageBaseReq;
import com.meteor.chat.common.mapper.ContactMapper;
import com.meteor.chat.common.mapper.RoomMapper;
import com.meteor.chat.common.util.CursorUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {

    /**
     * 游标分页获取用户的私人会话列表
     * @param req 游标分页请求
     * @param uid 用户id
     * @return
     */
    public CursorPageBaseResp<Contact> cursorPage(CursorPageBaseReq req, Long uid) {
        return CursorUtils.cursorPage(req, this,
                (wrapper) -> wrapper.eq(Contact::getUid, uid),
                Contact::getActiveTime);
    }

    public List<Contact> listByUid(Long uid) {
        return lambdaQuery().eq(Contact::getUid, uid).list();
    }
}
