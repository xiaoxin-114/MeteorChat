package com.meteor.chat.room.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResp {
    @Schema(description ="房间id")
    private Long roomId;
    @Schema(description ="群名称")
    private String groupName;
    @Schema(description ="群头像")
    private String avatar;
    @Schema(description ="在线人数")
    private Long onlineNum;
    /**
     * @see com.meteor.chat.room.enums.GroupRoleAPPEnum
     */
    @Schema(description ="成员角色 1群主 2管理员 3普通成员 4踢出群聊")
    private Integer role;
}
