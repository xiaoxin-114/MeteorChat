package com.meteor.chat.msg.service.impl;

import com.meteor.chat.chat.dao.ContactDao;
import com.meteor.chat.common.domain.dto.MsgReadInfoDTO;
import com.meteor.chat.common.domain.entity.Contact;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.enums.ReadEnum;
import com.meteor.chat.common.domain.vo.ChatMessageReadResp;
import com.meteor.chat.common.domain.vo.CursorPageBaseResp;
import com.meteor.chat.common.domain.vo.req.MessageReadCursorPageReq;
import com.meteor.chat.common.domain.vo.req.MessageReadInfoReq;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.msg.service.MessageService;
import com.meteor.chat.msg.service.adapter.MsgAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageDao messageDao;

    @Resource
    private ContactDao contactDao;

    @Override
    public CursorPageBaseResp<ChatMessageReadResp> cursorPageMsgReader(MessageReadCursorPageReq req) {
        Long msgId = req.getMsgId();
        Message message = messageDao.getById(msgId);
        CursorPageBaseResp<Contact> contactPage;
        // 获取未读的contact列表
        if (ReadEnum.UNREAD.getCode().equals(req.getSearchType())) {
            contactPage  = contactDao.cursorUnReadPage(req, message.getRoomId(), message.getCreateTime());
        }else {
            contactPage = contactDao.cursorReadPage(req, message.getRoomId(), message.getCreateTime());
        }
        List<Long> uidList = contactPage.getData().stream().map(Contact::getUid)
                .filter(id -> !message.getFromUid().equals(id)).collect(Collectors.toList());
        return CursorPageBaseResp.init(contactPage, uidList);
    }

    @Override
    public List<MsgReadInfoDTO> countReadAndUnRead(MessageReadInfoReq req, Long uid) {
        List<Long> idList = req.getMsgIds();
        List<Message> msgList = messageDao.listByIds(idList);
        msgList.forEach(msg -> {
            if (!uid.equals(msg.getFromUid())) {
                throw new BusinessException("只能查询自己发送的消息阅读数");
            }
        });
        List<Long> roomIds = msgList.stream().map(Message::getRoomId).collect(Collectors.toList());
        Assert.assertTrue("只能查询同一会话下的消息", roomIds.size() == 1);
        List<Contact> contactList = contactDao.listByRoomId(roomIds.get(0), uid);
        if (CollectionUtils.isEmpty(contactList)) {
            throw new BusinessException("会话信息缺失，计算失败");
        }
        return msgList.stream()
                .map(msg -> MsgAdapter.buildMsgReadInfoDTO(msg, contactList))
                .collect(Collectors.toList());
    }
}
