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
public class SummaryInfoReq {
    @Schema(description ="用户信息入参")
    @Size(max = 50)
    private List<infoReq> reqList;

    @Data
    public static class infoReq {
        @Schema(description ="uid")
        private Long uid;
        @Schema(description ="最近一次更新用户信息时间")
        private Long lastModifyTime;
    }
}
