package com.meteor.chat.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Description: 修改用户名
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummaryInfoDTO {
    @Schema(description = "用户id")
    private Long uid;
    @Schema(description = "是否需要刷新")
    private Boolean needRefresh = Boolean.TRUE;
    @Schema(description = "用户昵称")
    private String name;
    @Schema(description = "用户头像")
    private String avatar;
    @Schema(description = "归属地")
    private String locPlace;
    @Schema(description = "佩戴的徽章id")
    private Long wearingItemId;
    @Schema(description = "用户拥有的徽章id列表")
    List<Long> itemIds;

    public static SummaryInfoDTO skip(Long uid) {
        SummaryInfoDTO dto = new SummaryInfoDTO();
        dto.setUid(uid);
        dto.setNeedRefresh(Boolean.FALSE);
        return dto;
    }
}
