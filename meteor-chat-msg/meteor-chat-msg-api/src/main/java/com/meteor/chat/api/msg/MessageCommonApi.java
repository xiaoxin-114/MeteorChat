package com.meteor.chat.api.msg;

import com.meteor.chat.api.msg.constants.ApiConstants;
import com.meteor.chat.api.msg.dto.*;
import com.meteor.chat.common.result.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface MessageCommonApi {

    /**
     * 发送好友申请信息
     */
    @PostMapping(ApiConstants.MSG_PREFIX + "/user/apply")
    void sendUserApplyMsg( UserApplyMsgDTO userApplyMsgDTO);

    /**
     * 发送群成员被邀请成功入群信息
     */
    @PostMapping(ApiConstants.MSG_PREFIX + "/member/add")
    void sendMemberAddMsg( MemberAddMsgDTO memberAddMsgDTO);

    /**
     * 发送群成员退群信息
     */
    @PostMapping(ApiConstants.MSG_PREFIX + "/member/exit")
    void sendMemberExitMsg( BaseMemberChangDTO memberChangDTO);

    /**
     * 发送群成员被移除信息
     */
    @PostMapping(ApiConstants.MSG_PREFIX + "/member/remove")
    void sendMemberRemovedMsg( BaseMemberChangDTO memberChangDTO);

    /**
     * 获取群聊展示所需的用户相关信息
     * @return 最新消息缩略信息，未读消息数
     */
    @PostMapping(ApiConstants.MSG_PREFIX + "/room/list")
    ApiResult<List<RoomMsgDTO>> getRoomMsgList( List<RoomMsgReqDTO> reqList);

    /**
     * 删除群聊信息
     */
    @DeleteMapping(ApiConstants.MSG_PREFIX)
    void removeRoomMsg( Long roomId);

    /**
     * 获取消息的详情，用于推送给用户
     * @param msgId 消息id
     */
    @GetMapping(ApiConstants.MSG_PREFIX + "/detail")
    ApiResult<ChatMessageResp> getMessageResp( Long msgId);
}