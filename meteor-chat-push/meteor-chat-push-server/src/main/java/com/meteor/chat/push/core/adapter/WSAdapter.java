package com.meteor.chat.push.core.adapter;

import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.api.user.dto.WxQrCodeDTO;
import com.meteor.chat.push.common.domain.enums.WSRespTypeEnum;
import com.meteor.chat.push.common.domain.vo.*;

public class WSAdapter {

    public static WSBaseResp<WSLoginUrl> buildLoginResp(WxQrCodeDTO wxQrCodeDTO) {
        WSBaseResp<WSLoginUrl> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.LOGIN_URL.getType());
        wsBaseResp.setData(WSLoginUrl.builder().loginUrl(wxQrCodeDTO.getQrCodeUrl()).build());
        return wsBaseResp;
    }

    public static WSBaseResp buildScanSuccessResp() {
        WSBaseResp<Object> resp = new WSBaseResp<>();
        resp.setType(WSRespTypeEnum.LOGIN_SCAN_SUCCESS.getType());
        return resp;
    }

    public static WSBaseResp<WSLoginSuccess> buildLoginSuccessResp(UserInfoDTO userInfoDTO, String token) {
        WSBaseResp<WSLoginSuccess> resp = new WSBaseResp<>();
        WSLoginSuccess loginSuccess = WSLoginSuccess.builder()
                .avatar(userInfoDTO.getAvatar())
                .name(userInfoDTO.getName())
                .power(userInfoDTO.getRoleId() == null ? 0 : userInfoDTO.getRoleId())
                .uid(userInfoDTO.getUid())
                .token(token)
                .build();
        resp.setData(loginSuccess);
        resp.setType(WSRespTypeEnum.LOGIN_SUCCESS.getType());
        return resp;
    }

    public static WSBaseResp<?> buildTokenInvalidResp() {
        WSBaseResp<Object> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return wsBaseResp;
    }


}
