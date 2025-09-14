package com.meteor.chat.msg.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMsgMark implements Serializable {
    private List<WSMsgMarkItem> markList;

    @Data
    public static class WSMsgMarkItem {

        private Long uid;

        private Long msgId;
        /**
         * @see com.meteor.chat.msg.enums.MessageMarkTypeEnum
         */
        private Integer markType;

        private Long markCount;

        private Integer actType;
    }
}
