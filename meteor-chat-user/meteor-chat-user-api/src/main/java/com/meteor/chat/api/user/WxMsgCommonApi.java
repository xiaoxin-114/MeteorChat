package com.meteor.chat.api.user;

import com.meteor.chat.api.user.constants.ApiConstants;
import com.meteor.chat.api.user.dto.WxQrCodeDTO;
import com.meteor.chat.common.constants.RpcConstants;
import com.meteor.chat.common.result.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


public interface WxMsgCommonApi {

    /**
     * 获取微信登陆二维码
     * @param code 随机数，用于生成二维码
     * @param expireTime 秒数，过期时间
     */
    ApiResult<WxQrCodeDTO> getWxQrCode(Integer code, int expireTime);
}