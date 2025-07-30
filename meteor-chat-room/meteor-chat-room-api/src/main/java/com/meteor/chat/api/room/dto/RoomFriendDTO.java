package com.meteor.chat.api.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomFriendDTO {
    private Long roomId;

    private Long uid1;

    private Long uid2;

    public boolean hasUid(Long uid) {
        return uid1.equals(uid) || uid2.equals(uid);
    }
}
