package com.meteor.chat.api.msg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 基础的群成员变动消息DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseMemberChangDTO implements Serializable {
    private Long roomId;
    private Long uid;
}
