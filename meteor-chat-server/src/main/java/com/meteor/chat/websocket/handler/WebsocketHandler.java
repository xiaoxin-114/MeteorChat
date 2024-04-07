package com.meteor.chat.websocket.handler;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.meteor.chat.websocket.domain.enums.WSReqTypeEnum;
import com.meteor.chat.websocket.domain.vo.WSBaseReq;
import com.meteor.chat.websocket.service.WebSocketService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Sharable
public class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.webSocketService = getService();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        WSBaseReq wsBaseReqVO = JSONUtil.toBean(textWebSocketFrame.text(), WSBaseReq.class);
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

    private WebSocketService getService() {
        return SpringUtil.getBean(WebSocketService.class);
    }
}
