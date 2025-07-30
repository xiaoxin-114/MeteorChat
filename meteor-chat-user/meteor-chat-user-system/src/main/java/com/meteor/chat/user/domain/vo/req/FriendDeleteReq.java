package com.meteor.chat.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendDeleteReq {

    @NotNull
    @ApiModelProperty("删除的好友id")
    private Long friendId;
}
