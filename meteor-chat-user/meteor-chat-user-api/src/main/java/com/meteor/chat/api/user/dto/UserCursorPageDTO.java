package com.meteor.chat.api.user.dto;

import com.meteor.chat.mybatis.domain.CursorPageBaseReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCursorPageDTO extends CursorPageBaseReq implements Serializable {
    private Long roomId;

    private List<Long> uidList;
}
