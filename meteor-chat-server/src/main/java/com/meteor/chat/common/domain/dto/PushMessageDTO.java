package com.meteor.chat.common.domain.dto;

import com.meteor.chat.websocket.domain.vo.WSBaseResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushMessageDTO {
    public static final Integer ALL = 2;
    public static final Integer NOT_ALL = 1;

    private WSBaseResp wsBaseResp;

    private List<Long> uidList;
    /**
     * 是否发送给全员的消息
     */
    private Integer type;

    public PushMessageDTO(WSBaseResp wsBaseResp, Long uid) {
        this.wsBaseResp = wsBaseResp;
        this.uidList = Collections.singletonList(uid);
        this.type = NOT_ALL;
    }

    public PushMessageDTO(WSBaseResp wsBaseResp) {
        this.wsBaseResp = wsBaseResp;
        this.type = ALL;
    }
}
