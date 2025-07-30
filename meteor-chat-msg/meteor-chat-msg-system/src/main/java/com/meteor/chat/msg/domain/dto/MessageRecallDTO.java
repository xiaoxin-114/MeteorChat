package com.meteor.chat.msg.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRecallDTO implements Serializable {

    private Long msgId;
    private Long roomId;
    //撤回的用户
    private Long recallUid;

}

