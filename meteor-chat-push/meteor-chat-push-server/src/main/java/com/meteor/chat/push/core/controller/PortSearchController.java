package com.meteor.chat.push.core.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.meteor.chat.common.result.ApiResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/capi/websocket")
public class PortSearchController {

    @Resource
    @Lazy
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    private Integer websocketPort;

    @GetMapping("/public/url")
    public ApiResult<String> searchUrl() {
        return ApiResult.success("ws://" + nacosDiscoveryProperties.getIp() + ":" + websocketPort);
    }

    public void setWebsocketPort(Integer websocketPort) {
        this.websocketPort = websocketPort;
    }
}
