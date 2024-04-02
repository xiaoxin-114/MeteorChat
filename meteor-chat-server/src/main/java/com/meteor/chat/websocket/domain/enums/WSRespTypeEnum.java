package com.meteor.chat.websocket.domain.enums;

import com.meteor.chat.websocket.domain.vo.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  WebSocket响应类型枚举
 */
@Getter
@AllArgsConstructor
public enum WSRespTypeEnum {
    LOGIN_URL(1, "登录二维码返回", WSLoginUrl.class),
    LOGIN_SCAN_SUCCESS(2, "用户扫描成功等待授权", null),
    LOGIN_SUCCESS(3, "用户登录成功返回用户信息", WSLoginSuccess.class),
    MESSAGE(4, "新消息", WSMessage.class),
    ONLINE_OFFLINE_NOTIFY(5, "上下线通知", WSOnlineOfflineNotify.class),
    INVALIDATE_TOKEN(6, "使前端的token失效，意味着前端需要重新登录", null),
    BLACK(7, "拉黑用户", WSBlack.class);


    private Integer type;
    private String desc;
    private Class dataClass;
}
