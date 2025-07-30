package com.meteor.chat.room.api;

import com.meteor.chat.api.room.ContactCommonApi;
import com.meteor.chat.api.room.dto.ContactInfoDTO;
import com.meteor.chat.api.room.dto.MessageReadCursorPageDTO;
import com.meteor.chat.api.room.dto.ReadMessageDTO;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.room.service.ContactService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
@RestController
public class ContactCommonApiImpl implements ContactCommonApi {

    @Resource
    private ContactService contactService;

    @Override
    public CursorPageBaseResp<ContactInfoDTO> cursorReadPage(MessageReadCursorPageDTO req) {
        return contactService.cursorMsgReadOrUnReadPage(req, true);
    }

    @Override
    public CursorPageBaseResp<ContactInfoDTO> cursorUnReadPage(MessageReadCursorPageDTO req) {
        return contactService.cursorMsgReadOrUnReadPage(req, false);
    }

    @Override
    public List<ContactInfoDTO> listByRoomId(Long roomId, Long uid) {
        return contactService.listContactInfoByRoomId(roomId, uid);
    }

    @Override
    public ContactInfoDTO getByRoomIdUid(Long roomId, Long uid) {
        return contactService.getContactInfoByRoomIdUid(roomId, uid);
    }

    @Override
    public void readMsg(ReadMessageDTO req) {
        contactService.readMsg(req.getRoomId(), req.getUid());
    }
}
