package com.meteor.chat.user.domain.vo.req;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEmojiAddReq {
    /**
     * 表情地址
     */
    @Schema(description ="新增的表情url")
    private String expressionUrl;

}
