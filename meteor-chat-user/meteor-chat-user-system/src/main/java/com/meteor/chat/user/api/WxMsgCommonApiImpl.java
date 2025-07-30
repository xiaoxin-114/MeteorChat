package com.meteor.chat.user.api;

import com.meteor.chat.api.user.WxMsgCommonApi;
import com.meteor.chat.api.user.dto.WxQrCodeDTO;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class WxMsgCommonApiImpl implements WxMsgCommonApi {

    @Resource
    private WxMpService wxMpService;
    @Override
    public WxQrCodeDTO getWxQrCode(Integer code, int expireTime) {
        try {
            WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, expireTime);
            return new WxQrCodeDTO(wxMpQrCodeTicket.getUrl());
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
