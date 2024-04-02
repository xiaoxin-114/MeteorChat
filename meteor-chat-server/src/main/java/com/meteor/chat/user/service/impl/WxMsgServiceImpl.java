package com.meteor.chat.user.service.impl;

import com.meteor.chat.user.service.WxMsgService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Service;

@Service
public class WxMsgServiceImpl implements WxMsgService {
    @Override
    public WxMpXmlOutMessage scan(WxMpService service, WxMpXmlMessage message) {

        return null;
    }
}
