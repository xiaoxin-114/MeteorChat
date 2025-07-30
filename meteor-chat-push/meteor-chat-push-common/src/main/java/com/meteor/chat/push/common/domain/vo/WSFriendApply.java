package com.meteor.chat.push.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSFriendApply implements Serializable {

    private Long uid;

    private Integer unreadCount;

}
