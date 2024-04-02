package com.meteor.chat.user.controller;

import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.user.service.WxMsgService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/wx/portal/public")
public class WxProtalController {

    @Resource
    private WxMpService wxMpService;

    @Resource
    private WxMsgService wxMsgService;

    @RequestMapping("/callBack")
    public ApiResult authorizeCallBack(@RequestParam String code) throws WxErrorException {
        WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
        //todo 获取到accessToken后，可能要校验会刷新accessToken
        WxOAuth2UserInfo userInfo = wxMpService.getOAuth2Service().getUserInfo(accessToken, null);
        wxMsgService.authorize(userInfo);
        return ApiResult.success("授权成功");
    }

}
