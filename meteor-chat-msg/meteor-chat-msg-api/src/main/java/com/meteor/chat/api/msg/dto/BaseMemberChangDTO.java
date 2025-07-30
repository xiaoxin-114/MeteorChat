package com.meteor.chat.api.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基础的群成员变动消息DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseMemberChangDTO {
    private Long roomId;
    private Long uid;
}
