package com.meteor.chat.websocket.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.meteor.chat.common.constants.RedisKey;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.entity.UserRole;
import com.meteor.chat.common.util.RedisUtils;
import com.meteor.chat.event.UserOfflineEvent;
import com.meteor.chat.event.UserOnlineEvent;
import com.meteor.chat.user.dao.UserDao;
import com.meteor.chat.user.dao.UserRoleDao;
import com.meteor.chat.user.service.LoginService;
import com.meteor.chat.user.service.cache.UserCache;
import com.meteor.chat.websocket.adapter.WSAdapter;
import com.meteor.chat.websocket.domain.dto.WSChannelExtraDTO;
import com.meteor.chat.websocket.domain.vo.WSAuthorize;
import com.meteor.chat.websocket.domain.vo.WSBaseResp;
import com.meteor.chat.websocket.service.WebSocketService;
import com.meteor.chat.websocket.util.NettyUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class WebSocketServiceImpl  implements WebSocketService {

    private static final Duration EXPIRE_TIME = Duration.ofHours(1);
    private static final Long MAX_MUM_SIZE = 10000L;
    /**
     * 所有请求登录的code与channel关系，存储还未登入的code和channel映射关系，登录成功后删除映射
     */
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_TIME)
            .maximumSize(MAX_MUM_SIZE)
            .build();
    /**
     * 所有已连接的websocket连接列表和一些额外参数
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();
    /**
     * 所有在线的用户和对应的socket，之所以value是个channel列表，应该是一个账号支持多地登入
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Channel, WSChannelExtraDTO> getOnlineMap() {
        return ONLINE_WS_MAP;
    }

    /**
     * redis保存loginCode的key
     */
    private static final String LOGIN_CODE = "loginCode";
    @Autowired
    private WxMpService wxMpService;
    @Resource
    private LoginService loginService;
    @Resource
    private UserDao userDao;
    @Resource
    private UserRoleDao userRoleDao;
    @Resource
    private UserCache userCache;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void handleLoginReq(Channel channel) throws WxErrorException {
        //生成一个不重复的随机数
        Integer code = generateLoginCode(channel);
        //根据随机数向微信申请一个带有参数的临时二维码
        WxMpQrCodeTicket qrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) EXPIRE_TIME.getSeconds());
        //将二维码返回给前端
        sendMsg(channel, WSAdapter.buildLoginResp(qrCodeTicket));
    }

    /**
     * 完成websocket连接后
     * @param channel
     */
    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    /**
     * channel断开连接，用户离线
     * @param channel
     */
    @Override
    public void removed(Channel channel) {
        WSChannelExtraDTO dto = ONLINE_WS_MAP.get(channel);
        Optional<Long> uid = Optional.ofNullable(dto).map(d -> d.getUid());
        boolean offSuccess = offline(channel, uid);
        if (uid.isPresent() && offSuccess) {
            User user = User.builder()
                    .id(uid.get())
                    .lastOptTime(new Date())
                    .build();
            //发送一个用户离线的事件
            applicationEventPublisher.publishEvent(new UserOfflineEvent(this, user));
        }
    }

    /**
     * 前端通过websocket连接进行主动认证
     * @param channel
     * @param wsAuthorize
     */
    @Override
    public void authorize(Channel channel, WSAuthorize wsAuthorize) {
        String token = wsAuthorize.getToken();
        if (!loginService.verify(token)) {
            sendMsg(channel, WSAdapter.buildTokenInvalidResp());
        } else {
            Long uid = loginService.getValidUid(token);
            successLogin(channel, userDao.getById(uid), token);
        }
    }

    @Override
    public Boolean scanLoginSuccess(Integer loginCode, Long uid) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (channel == null) {
            return false;
        }
        // 登入成功后，清除code和channel的映射关系
        WAIT_LOGIN_MAP.invalidate(loginCode);
        String token = loginService.login(uid);
        User user = userDao.getById(uid);
        successLogin(channel, user, token);
        return true;
    }

    @Override
    public Boolean scanSuccess(Integer loginCode) {
        // 根据loginCode获取对应的channel，使用channel发送消息，告知用户扫码成功
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (channel != null){
            sendMsg(channel, WSAdapter.buildScanSuccessResp());
            return true;
        }
        return false;
    }

    @Override
    public void sendToUid(WSBaseResp<?> wsBaseResp, Long uid) {

    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp) {

    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {

    }

    /**
     * 获取不重复的登录的code，微信要求最大不超过int的存储极限
     * 防止并发，可以给方法加上synchronize，也可以使用cas乐观锁
     *
     * @return
     */
    private Integer generateLoginCode(Channel channel) {
        int inc;
        do {
            //本地cache时间必须比redis key过期时间短，否则会出现并发问题
            inc = RedisUtils.integerInc(RedisKey.getKey(LOGIN_CODE), (int) EXPIRE_TIME.toMinutes(), TimeUnit.MINUTES);
            //猜测，为啥不更新redis中的LOGIN_CODE的值，不然每次从头开始只能
        } while (WAIT_LOGIN_MAP.asMap().containsKey(inc));
        //储存一份在本地
        WAIT_LOGIN_MAP.put(inc, channel);
        return inc;
    }

    /**
     * 给本地channel发送消息
     *
     * @param channel
     * @param wsBaseResp
     */
    private void sendMsg(Channel channel, WSBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }

    /**
     * 用户成功登录后调用的方法，更新上线列表，告知前端用户登录成功，更新用户的相关信息等
     * @param channel
     * @param user
     * @param token
     */
    private void successLogin(Channel channel, User user, String token) {
        //更新用户在线列表
        online(channel, user.getId());
        //告知前端用户登陆成功，需要告知前端用户的角色
        UserRole role = userRoleDao.getUserRoleByUid(user.getId());
        sendMsg(channel, WSAdapter.buildLoginSuccessResp(user, token,
                Optional.ofNullable(role)
                        .map(r -> r.getRoleId())
                        .orElse(null)));
        if (!userCache.isOnline(user.getId())){
            //如果用户之前是离线状态，那么就更新用户的状态信息
            user.setLastOptTime(new Date());
            // 更新用户的ip信息
            user.refreshIp(NettyUtils.getAttr(channel, NettyUtils.IP_KEY));
            //发送用户登陆的事件
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
    }

    /**
     * 用户上线，更新在线的channel列表与其对应的uid
     * @param uid
     */
    private void online(Channel channel, Long uid) {
        getOrInitChannelCtx(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtils.setAttr(channel, NettyUtils.UID_KEY, uid);
    }

    /**
     * 用户离线，移除ONLINE_WS_MAP中的channel，并更新ONLINE_UID_MAP中的uid对应的channel列表
     * @param channel
     * @param uid
     * @return ONLINE_UID_MAP中uid对应的channel是否移除完
     */
    private boolean offline(Channel channel, Optional<Long> uid) {
        ONLINE_WS_MAP.remove(channel);
        if (uid.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid.get());
            channels.forEach(uChannel -> {
                if (uChannel == channel) {
                    channels.remove(uChannel);
                }
            });
            return CollectionUtils.isEmpty(channels);
        }
        return true;
    }

    /**
     * 获取通道对应的dto对象，没有则初始化
     * @param channel
     * @return
     */
    private WSChannelExtraDTO getOrInitChannelCtx(Channel channel) {
        WSChannelExtraDTO newDto = new WSChannelExtraDTO();
        WSChannelExtraDTO extraDTO = ONLINE_WS_MAP.putIfAbsent(channel, newDto);
        return extraDTO == null ? newDto : extraDTO;
    }

}
