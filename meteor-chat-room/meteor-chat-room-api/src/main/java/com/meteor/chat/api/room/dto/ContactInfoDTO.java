package com.meteor.chat.api.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactInfoDTO {

    private Long uid;

    private Long roomId;

    private Date readTime;

    private Long lastMsgId;
}
