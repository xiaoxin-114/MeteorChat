package com.meteor.chat.common.domain.vo.websocket;

import lombok.Data;

/**
 * websocket基础请求，类型1-请求登入二维码，类型2-心跳包
 */
@Data
public class WSBaseReqVO {

    private Integer type;
    private String data;
}
