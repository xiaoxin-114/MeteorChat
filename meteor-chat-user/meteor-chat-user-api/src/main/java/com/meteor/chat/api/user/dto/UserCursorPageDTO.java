package com.meteor.chat.api.user.dto;

import com.meteor.chat.mybatis.domain.CursorPageBaseReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCursorPageDTO extends CursorPageBaseReq {
    private Long roomId;

    private List<Long> uidList;
}
