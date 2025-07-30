package com.meteor.chat.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDTO {
    private Long uid;
    private String avatar;
    private String name;

    private Long roleId;

    private Date lastOptTime;

    private Integer activeStatus;
}
