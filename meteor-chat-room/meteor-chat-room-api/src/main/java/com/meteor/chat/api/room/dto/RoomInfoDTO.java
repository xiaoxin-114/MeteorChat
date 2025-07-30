package com.meteor.chat.api.room.dto;

import com.meteor.chat.api.room.enums.HotFlagEunm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomInfoDTO {
    /**
     * id
     */
    private Long roomId;

    /**
     * 房间类型 1群聊 2单聊
     */
    private Integer type;

    /**
     * 是否全员展示 0否 1是
     */
    private Integer hotFlag;

    public boolean isHotRoom() {
        return  HotFlagEunm.HOT_ROOM.getCode() == hotFlag;
    }

}
