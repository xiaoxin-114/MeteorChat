package com.meteor.chat.room.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberResp implements Serializable {
    @Schema(description ="uid")
    private Long uid;

    @Schema(description ="在线状态 1在线 2离线")
    private Integer activeStatus;

    /**
     * 角色ID
     */
    private Integer roleId;

    @Schema(description ="最后一次上下线时间")
    private Date lastOptTime;
}
