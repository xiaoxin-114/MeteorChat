package com.meteor.chat.user.utils;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.user.domain.dto.IpResultDTO;
import com.meteor.chat.user.domain.entity.IpDetail;
import com.meteor.chat.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
@Component
@Slf4j
public class IPUtils implements DisposableBean {
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(500), new NamedThreadFactory(CommonConstants.IP_EXECUTOR, false));
    private final String URL = "https://ip.taobao.com/outGetIpInfo?ip={%s}&accessKey=alibaba-inc";

    public IpDetail asyncGetIpDetail(String ip) throws ExecutionException, InterruptedException {
        String url = URL.replace("{%s}", ip);
        Future<IpDetail> future = executor.submit(() -> {
            for (int i = 0; i < CommonConstants.GET_IPINFO_RETRY; i++) {
                String result = HttpUtil.get(url);
                IpResultDTO<IpDetail> resultDTO = JsonUtils.toObj(result, new TypeReference<IpResultDTO<IpDetail>>() {});
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

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
        // 阻塞等待30s
        boolean termination = executor.awaitTermination(30, TimeUnit.SECONDS);
        if (termination) {
            log.info("线程池{}已经停止", executor);
        } else {
            log.error("Timed out while waiting for executor [{}] to terminate", executor);
        }
    }
}
