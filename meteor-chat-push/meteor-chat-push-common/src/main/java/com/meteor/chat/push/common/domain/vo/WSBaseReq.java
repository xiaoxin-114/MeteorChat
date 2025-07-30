package com.meteor.chat.push.common.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * websocket基础请求，类型1-请求登入二维码，类型2-心跳包
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSBaseReq implements Serializable {

    private Integer type;
    private String data;
}
