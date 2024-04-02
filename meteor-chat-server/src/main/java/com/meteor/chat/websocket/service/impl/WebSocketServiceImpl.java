package com.meteor.chat.websocket.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.meteor.chat.common.constants.RedisKey;
import com.meteor.chat.common.util.RedisUtils;
import com.meteor.chat.websocket.adapter.WSAdapter;
import com.meteor.chat.websocket.domain.dto.WSChannelExtraDTO;
import com.meteor.chat.websocket.domain.vo.WSBaseResp;
import com.meteor.chat.websocket.service.WebSocketService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class WebSocketServiceImpl  implements WebSocketService {

    private static final Duration EXPIRE_TIME = Duration.ofHours(1);
    private static final Long MAX_MUM_SIZE = 10000L;
    /**
     * 所有请求登录的code与channel关系
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
     * 所有在线的用户和对应的socket
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
}
