package com.meteor.chat.common.constants;

public interface CommonConstants {

    String USER_APPROVAL_MSG_CONTENT = "我们已经成为好友了，开始聊天吧";
    int USER_BACK_PACK_USED = 1;
    int USER_BACK_PACK_NOT_USED = 0;
    int USER_EMOJI_MAX_NUM = 30;
    // 解析ip归属地线程名称
    String IP_EXECUTOR = "refresh-ipDetail";
    // 解析ip归属地最大重试次数
    int GET_IPINFO_RETRY = 3;
    // 解析ip归属地失败时间间隔
    long GET_IPINFO_RETRY_INTERVAL = 3 * 1000;

    int MAX_ADMIN_NUM = 3;

    int CAN_CALLBACK_GAP_MAX_COUNT = 100;
    /**
     * 系统消息的发送用户
     */
    Long SYSTEM_UID = 1L;

    String TRACE_ID_KEY = "traceId";
}
