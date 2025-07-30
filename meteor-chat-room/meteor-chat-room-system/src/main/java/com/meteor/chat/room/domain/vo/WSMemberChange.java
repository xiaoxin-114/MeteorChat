package com.meteor.chat.room.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WSMemberChange implements Serializable {
    public static final Integer CHANGE_TYPE_ADD = 1;
    public static final Integer CHANGE_TYPE_REMOVE = 2;

    private Long uid;

    private Long roomId;

    /**
     * 成员变动的类型，加入群聊或者移出群聊
     */
    private Integer changeType;
    /**
     * 该用户的在线状态
     **/
    private Integer activeStatus;

    private Date lastOptTime;
}
