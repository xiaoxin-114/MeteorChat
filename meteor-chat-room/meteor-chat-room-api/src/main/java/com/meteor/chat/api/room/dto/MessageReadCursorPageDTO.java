package com.meteor.chat.api.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageReadCursorPageDTO implements Serializable {

    private Long roomId;

    private Date msgCreateTime;
    @Schema(description ="页面大小")
    private int pageSize;
    @Schema(description ="游标")
    private String cursor;
}
