package com.meteor.chat.websocket.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * websocket基础请求，类型1-请求登入二维码，类型2-心跳包
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSBaseReq {

    private Integer type;
    private String data;
}
