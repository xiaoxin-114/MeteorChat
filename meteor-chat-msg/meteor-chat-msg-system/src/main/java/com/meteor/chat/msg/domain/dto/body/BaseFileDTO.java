package com.meteor.chat.msg.domain.dto.body;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseFileDTO implements Serializable {

    private static final long serialVersionUID = -6110490245902742033L;
    @Schema(description = "大小（字节）")
    @NotNull
    private Long size;

    @Schema(description = "下载地址")
    @NotBlank
    private String url;
}