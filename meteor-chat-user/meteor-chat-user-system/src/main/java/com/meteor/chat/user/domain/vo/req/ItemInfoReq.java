package com.meteor.chat.user.domain.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoReq {
    @Schema(description ="徽章信息入参")
    @Size(max = 50)
    private List<infoReq> reqList;

    @Data
    public static class infoReq {
        @Schema(description ="徽章id")
        private Long itemId;
        @Schema(description ="最近一次更新徽章信息时间")
        private Long lastModifyTime;
    }
}
