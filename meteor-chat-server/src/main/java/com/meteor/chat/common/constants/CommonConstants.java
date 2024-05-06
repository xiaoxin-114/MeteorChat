package com.meteor.chat.common.constants;

public interface CommonConstants {

    int USER_BACK_PACK_USED = 1;
    int USER_BACK_PACK_NOT_USED = 0;
    // 解析ip归属地线程名称
    String IP_EXECUTOR = "refresh-ipDetail";
    // 解析ip归属地最大重试次数
    int GET_IPINFO_RETRY = 3;
    // 解析ip归属地失败时间间隔
    long GET_IPINFO_RETRY_INTERVAL = 3 * 1000;
}
