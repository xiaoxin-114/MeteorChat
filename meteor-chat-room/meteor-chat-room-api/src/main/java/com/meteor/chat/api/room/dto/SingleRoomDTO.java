package com.meteor.chat.api.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SingleRoomDTO implements Serializable {
    private Long uid1;
    private Long uid2;
}
