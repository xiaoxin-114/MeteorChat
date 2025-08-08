package com.meteor.chat.api.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserApplyMsgDTO implements Serializable {
    private Long uid;
    private Long roomId;
    private String msg;
}
