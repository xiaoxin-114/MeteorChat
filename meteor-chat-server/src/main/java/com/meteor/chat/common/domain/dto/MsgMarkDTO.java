package com.meteor.chat.common.domain.dto;

import com.meteor.chat.common.domain.vo.req.MsgMarkReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsgMarkDTO {

    private Integer markType;

    private Integer actType;

    private Long msgId;

    private Long uid;

    public MsgMarkDTO(MsgMarkReq req, Long uid) {
        this.markType = req.getMarkType();
        this.actType = req.getActType();
        this.msgId = req.getMsgId();
        this.uid = uid;
    }
}
