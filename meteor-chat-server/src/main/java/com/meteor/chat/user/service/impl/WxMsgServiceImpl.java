package com.meteor.chat.user.service.impl;

import com.meteor.chat.common.constants.MQConstant;
import com.meteor.chat.common.constants.RedisKey;
import com.meteor.chat.common.domain.dto.LoginMessageDTO;
import com.meteor.chat.common.domain.dto.ScanSuccessMessageDTO;
import com.meteor.chat.common.domain.entity.IpInfo;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.entity.UserRole;
import com.meteor.chat.common.util.RedisUtils;
import com.meteor.chat.transaction.service.MQProducer;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.dao.UserRoleDao;
import com.meteor.chat.user.service.UserService;
import com.meteor.chat.user.service.WxMsgService;
import com.meteor.chat.user.service.adapter.TextBuilder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class WxMsgServiceImpl implements WxMsgService {
    private static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    /**
     * 用户授权完成后，回调的url
     */
    @Value("${wx.mp.callback}")
    private String callback;
    @Resource
    private UserDao userDao;
    @Resource
    private UserService userService;
    @Resource
    private MQProducer mqProducer;
    @Resource
    private UserRoleDao userRoleDao;

    @Override
    @Transactional
    public WxMpXmlOutMessage scan(WxMpService service, WxMpXmlMessage message) {
        String openId = message.getFromUser();
        User user = userDao.getByOpenId(openId);
        // 如果用户已经注册，那么说明已经登入成功即可
        int loginCode = Integer.parseInt(this.getEventKey(message));
        if (Objects.nonNull(user) && StringUtils.isNotEmpty(user.getAvatar())) {
            //mq发送消息，发送消息给前端登录成功
            mqProducer.sendMsg(MQConstant.LOGIN_MSG_TOPIC, new LoginMessageDTO(user.getId(), loginCode));
            return null;
        }
        // 如果未注册进行注册
        if (Objects.isNull(user)){
            user = User.builder().openId(openId).build();
            userService.register(user);
            UserRole userRole = new UserRole();
            userRole.setUid(userDao.getByOpenId(openId).getId());
            userRole.setRoleId(0L);
            userRoleDao.save(userRole);
        }
        //将openId与code的映射关系缓存到redis中
        RedisUtils.set(RedisKey.getKey(RedisKey.OPEN_ID_STRING, openId), loginCode, 60, TimeUnit.MINUTES);
        //使用mq异步发送消息给前端，表示已经扫码成功，等待授权
        mqProducer.sendMsg(MQConstant.SCAN_MSG_TOPIC, new ScanSuccessMessageDTO(loginCode));
        String url = String.format(AUTHORIZE_URL, service.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return new TextBuilder().build("点击下方链接进行授权：<a href=\"" + url + "\">授权</a>", message, service);
    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getByOpenId(openid);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setName(userInfo.getNickname());
        user.setSex(userInfo.getSex());
        try {
            userDao.updateById(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            user.setName("名字重置" + user.getId());
            userDao.updateById(user);
        }
        Integer code = RedisUtils.get(RedisKey.getKey(RedisKey.OPEN_ID_STRING, openid), Integer.class);
        //mq发送用户成功登陆的事件
        mqProducer.sendMsg(MQConstant.LOGIN_MSG_TOPIC, new LoginMessageDTO(user.getId(), code));
    }

    private String getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        //扫码关注的渠道事件有前缀，需要去除
        return wxMpXmlMessage.getEventKey().replace("qrscene_", "");
    }
}
