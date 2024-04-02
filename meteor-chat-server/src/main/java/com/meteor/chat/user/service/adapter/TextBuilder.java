package com.meteor.chat.user.service.adapter;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;

/**
 * 消息转换适配器，由wxMpXmlMessage对象转换成WxMpXmlOutTextMessage对象
 */
public class TextBuilder {

    /**
     * 构建消息的回应消息
     * @param content 回应的内容
     * @param wxMpXmlMessage 进行回应的消息
     * @param service
     * @return
     */
    public WxMpXmlOutTextMessage build(String content, WxMpXmlMessage wxMpXmlMessage,
                                       WxMpService service){
        WxMpXmlOutTextMessage message = WxMpXmlOutMessage.TEXT().content(content).fromUser(wxMpXmlMessage.getToUser()).toUser(wxMpXmlMessage.getFromUser()).build();
        return message;
    }
}
