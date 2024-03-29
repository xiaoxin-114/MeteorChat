package com.meteor.chat.websocket.handler;

import cn.hutool.json.JSONUtil;
import com.meteor.chat.common.domain.enums.WSReqTypeEnum;
import com.meteor.chat.common.domain.vo.websocket.WSBaseReqVO;
import com.meteor.chat.websocket.service.WebSocketService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        WSBaseReqVO wsBaseReqVO = JSONUtil.toBean(textWebSocketFrame.text(), WSBaseReqVO.class);
        WSReqTypeEnum typeEnum = WSReqTypeEnum.of(wsBaseReqVO.getType());
        switch (typeEnum){
            case LOGIN:
                webSocketService.handleLoginReq(channelHandlerContext.channel());
                log.info("请求登录二维码" + textWebSocketFrame.text());
                break;
            case HEARTBEAT:
                break;
            default:
                log.info("无法识别的websocket请求类型，该请求无效");
        }
    }
}
