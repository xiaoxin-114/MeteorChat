package com.meteor.chat.room.api;

import com.meteor.chat.api.room.ContactCommonApi;
import com.meteor.chat.api.room.dto.ContactInfoDTO;
import com.meteor.chat.api.room.dto.MessageReadCursorPageDTO;
import com.meteor.chat.api.room.dto.ReadMessageDTO;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.mybatis.domain.CursorPageBaseResp;
import com.meteor.chat.room.service.ContactService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class ContactCommonApiImpl implements ContactCommonApi {

    @Resource
    private ContactService contactService;

    @Override
    public ApiResult<CursorPageBaseResp<ContactInfoDTO>> cursorReadPage(MessageReadCursorPageDTO req) {
        return ApiResult.success(contactService.cursorMsgReadOrUnReadPage(req, true));
    }

    @Override
    public ApiResult<CursorPageBaseResp<ContactInfoDTO>> cursorUnReadPage(MessageReadCursorPageDTO req) {
        return ApiResult.success(contactService.cursorMsgReadOrUnReadPage(req, false));
    }

    @Override
    public ApiResult<List<ContactInfoDTO>> listByRoomId(Long roomId, Long uid) {
        return ApiResult.success(contactService.listContactInfoByRoomId(roomId, uid));
    }

    @Override
    public ApiResult<ContactInfoDTO> getByRoomIdUid(Long roomId, Long uid) {
        return ApiResult.success(contactService.getContactInfoByRoomIdUid(roomId, uid));
    }

    @Override
    public void readMsg(ReadMessageDTO req) {
        contactService.readMsg(req.getRoomId(), req.getUid());
    }
}
