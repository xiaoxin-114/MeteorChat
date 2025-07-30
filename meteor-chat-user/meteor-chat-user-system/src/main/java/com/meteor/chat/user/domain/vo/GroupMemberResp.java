package com.meteor.chat.user.domain.vo;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty("uid")
    private Long uid;
    /**
     * @see com.meteor.chat.user.enums.ChatActiveStatusEnum
     */
    @ApiModelProperty("在线状态 1在线 2离线")
    private Integer activeStatus;

    /**
     * 角色ID
     */
    private Integer roleId;

    @ApiModelProperty("最后一次上下线时间")
    private Date lastOptTime;
}
