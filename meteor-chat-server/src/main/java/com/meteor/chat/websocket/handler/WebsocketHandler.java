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

@Slf4j
@Sharable
public class WebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;

    /**
     * 与客户端建立websocket连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.webSocketService = getService();
    }

    /**
     * 与客户端端口websocket断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
    }

    /**
     * 处理客户端发送的websokcet消息
     * @param channelHandlerContext
     * @param textWebSocketFrame
     * @throws Exception
     */
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

    /**
     * channel掉线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
        userOffLine(ctx);
    }

    /**
     * 发送异常时
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发送异常{}", cause);
        ctx.channel().close();
    }

    /**
     * 处理客户端发送的心跳包
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //todo
    }

    private WebSocketService getService() {
        return SpringUtil.getBean(WebSocketService.class);
    }


    /**
     * 用户离线操作，在redis取消uid与channel的关联，关闭对应channel
     * @param ctx
     */
    private void userOffLine(ChannelHandlerContext ctx){
        webSocketService.removed(ctx.channel());
        ctx.channel().close();
    }
}
