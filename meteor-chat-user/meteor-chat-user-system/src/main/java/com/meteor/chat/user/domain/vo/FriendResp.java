package com.meteor.chat.user.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="好友列表游标分页响应")
public class FriendResp {

    @Schema(description ="好友uid")
    private Long uid;

    /**
     * @see com.meteor.chat.common.domain.enums.ChatActiveStatusEnum
     */
    @Schema(description ="在线状态 1在线 2离线")
    private Integer activeStatus;
}
