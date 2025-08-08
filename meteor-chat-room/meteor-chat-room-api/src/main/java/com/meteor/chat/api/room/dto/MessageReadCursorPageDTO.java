package com.meteor.chat.api.room.dto;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty("页面大小")
    private int pageSize;
    @ApiModelProperty("游标")
    private String cursor;
}
