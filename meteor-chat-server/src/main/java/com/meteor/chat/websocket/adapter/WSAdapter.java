package com.meteor.chat.websocket.adapter;

import com.meteor.chat.websocket.domain.enums.WSRespTypeEnum;
import com.meteor.chat.websocket.domain.vo.WSBaseResp;
import com.meteor.chat.websocket.domain.vo.WSLoginUrl;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;

public class WSAdapter {

    public static WSBaseResp<WSLoginUrl> buildLoginResp(WxMpQrCodeTicket wxMpQrCodeTicket) {
        WSBaseResp<WSLoginUrl> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        wsBaseResp.setData(WSLoginUrl.builder().loginUrl(wxMpQrCodeTicket.getUrl()).build());
        return wsBaseResp;
    }
}
