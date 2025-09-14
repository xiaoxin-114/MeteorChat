package com.meteor.chat.room.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class AdminChangeReq {
    @NotNull
    @Schema(description ="房间号")
    private Long roomId;

    @NotNull
    @Size(min = 1, max = 3)
    @Schema(description ="需要添加管理的列表")
    private List<Long> uidList;
}
