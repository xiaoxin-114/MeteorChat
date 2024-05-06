package com.meteor.chat.common.util;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.domain.dto.IpResultDTO;
import com.meteor.chat.common.domain.entity.IpDetail;

import java.util.concurrent.*;

public class IPUtils {
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(500), new NamedThreadFactory(CommonConstants.IP_EXECUTOR, false));
    private static final String URL = "https://ip.taobao.com/outGetIpInfo?ip={%s}&accessKey=alibaba-inc";

    public static IpDetail asyncGetIpDetail(String ip) throws ExecutionException, InterruptedException {
        String url = URL.replace("{%s}", ip);
        Future<IpDetail> future = executor.submit(() -> {
            for (int i = 0; i < CommonConstants.GET_IPINFO_RETRY; i++) {
                String result = HttpUtil.get(url);
                IpResultDTO<IpDetail> resultDTO = JSONObject.parseObject(result, IpResultDTO.class);
                if (resultDTO.isSuccess()) {
                    return resultDTO.getData();
                }
                // 等待3s，防止频限
                ThreadUtil.sleep(CommonConstants.GET_IPINFO_RETRY_INTERVAL, TimeUnit.MILLISECONDS);
            }
            return null;
        });
        return future.get();
    }
}
