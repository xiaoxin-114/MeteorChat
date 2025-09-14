package com.meteor.chat.user.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 表情包响应
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmojiResp {
    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 表情地址
     */
    @Schema(description = "表情url")
    private String expressionUrl;

}
