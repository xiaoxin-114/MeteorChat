package com.meteor.chat.api.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MsgSendMessageDTO implements Serializable {
    private static final long serialVersionUID = 5736586637960927836L;

    private Long msgId;
}
