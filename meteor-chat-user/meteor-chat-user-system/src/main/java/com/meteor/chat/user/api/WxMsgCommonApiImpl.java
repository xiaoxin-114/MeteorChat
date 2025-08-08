package com.meteor.chat.user.api;

import com.meteor.chat.api.user.WxMsgCommonApi;
import com.meteor.chat.api.user.dto.WxQrCodeDTO;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.common.result.ApiResult;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
@Slf4j
public class WxMsgCommonApiImpl implements WxMsgCommonApi {

    @Resource
    private WxMpService wxMpService;
    @Override
    public ApiResult<WxQrCodeDTO> getWxQrCode(Integer code, int expireTime) {
        try {
            WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, expireTime);
            return ApiResult.success(new WxQrCodeDTO(wxMpQrCodeTicket.getUrl()));
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
    }
}
