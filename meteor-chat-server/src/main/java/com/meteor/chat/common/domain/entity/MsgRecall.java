package com.meteor.chat.common.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgRecall implements Serializable {

    private Date recallTime;

    private Long recallUid;
}
