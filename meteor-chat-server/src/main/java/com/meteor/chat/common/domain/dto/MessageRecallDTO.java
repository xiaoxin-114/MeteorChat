package com.meteor.chat.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRecallDTO {

    private Long msgId;
    private Long roomId;
    //撤回的用户
    private Long recallUid;

}

