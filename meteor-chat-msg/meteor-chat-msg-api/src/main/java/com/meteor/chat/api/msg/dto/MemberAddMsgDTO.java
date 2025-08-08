package com.meteor.chat.api.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberAddMsgDTO implements Serializable {
    private Long roomId;
    private Long inviter;
    private List<Long> memberUidList;
}
