package com.meteor.chat.msg.api;

import com.meteor.chat.api.msg.MessageCommonApi;
import com.meteor.chat.api.msg.dto.*;
import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.result.ApiResult;
import com.meteor.chat.msg.adapter.MsgAdapter;
import com.meteor.chat.msg.service.MessageService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class MessageCommonApiImpl implements MessageCommonApi {

    @Resource
    private MessageService messageService;

    @Override
    public void sendUserApplyMsg(UserApplyMsgDTO userApplyMsgDTO) {
        messageService.sendMsg(MsgAdapter.buildApprovalMsg(userApplyMsgDTO.getRoomId(), userApplyMsgDTO.getMsg()), CommonConstants.SYSTEM_UID);
    }

    @Override
    public void sendMemberAddMsg(MemberAddMsgDTO memberAddMsgDTO) {
        messageService.sendMemberAddMsg(memberAddMsgDTO);
    }

    @Override
    public void sendMemberExitMsg(@RequestBody BaseMemberChangDTO memberChangDTO) {
        messageService.sendMemberSubMsg(memberChangDTO.getRoomId(), memberChangDTO.getUid(), " 退出了群聊");
    }

    @Override
    public void sendMemberRemovedMsg(@RequestBody BaseMemberChangDTO memberChangDTO) {
        messageService.sendMemberSubMsg(memberChangDTO.getRoomId(), memberChangDTO.getUid(), " 被管理员移出群聊");
    }

    @Override
    public ApiResult<List<RoomMsgDTO>> getRoomMsgList(List<RoomMsgReqDTO> reqList) {
        return ApiResult.success(messageService.getRoomMsgList(reqList));
    }

    @Override
    public void removeRoomMsg(Long roomId) {
        messageService.removeRoomMsg(roomId);
    }

    @Override
    public ApiResult<ChatMessageResp> getMessageResp(Long msgId) {
        return ApiResult.success(messageService.getMessageResp(msgId, null));
    }
}
