package com.meteor.chat.msg.service.handler.msg;

import com.meteor.chat.api.room.RoomCommonApi;
import com.meteor.chat.api.room.RoomMemberCommonApi;
import com.meteor.chat.api.user.UserInfoCommonApi;
import com.meteor.chat.api.user.dto.UserInfoDTO;
import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.enums.YesOrNoEnum;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.msg.domain.dto.body.TextMsgReq;
import com.meteor.chat.msg.domain.dto.body.TextMsgResp;
import com.meteor.chat.msg.domain.entity.Message;
import com.meteor.chat.msg.domain.entity.MessageExtra;
import com.meteor.chat.msg.domain.entity.UrlInfo;
import com.meteor.chat.msg.enums.MessageTypeEnum;
import com.meteor.chat.msg.urldiscover.PrioritizedUrlDiscover;
import com.meteor.chat.sensitiveword.core.SensitiveWords;
import org.junit.Assert;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgReq> {
    @Resource
    private MessageDao messageDao;

    @Resource
    private PrioritizedUrlDiscover prioritizedUrlDiscover;

    private final MessageTypeEnum MESSAGE_TYPE = MessageTypeEnum.TEXT;

    @Resource
    private UserInfoCommonApi userInfoCommonApi;

    @Resource
    private RoomMemberCommonApi roomMemberCommonApi;

    @Resource
    private RoomCommonApi roomcommonApi;

    @Resource
    private SensitiveWords sensitiveWords;

    @Override
    MessageTypeEnum getMsgType() {
        return MESSAGE_TYPE;
    }

    @Override
    void saveMessageExtra(Message message, TextMsgReq body) {
        MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Long replyMsgId = body.getReplyMsgId();
        Message update = new Message();
        update.setId(message.getId());
        update.setContent(body.getContent());
        if (Objects.nonNull(replyMsgId)) {
            update.setReplyMsgId(replyMsgId);
            update.setGapCount(messageDao.countMsgGap(message.getRoomId(), message.getId(), replyMsgId));
        }
        List<Long> atUidList = body.getAtUidList();
        if (!CollectionUtils.isEmpty(atUidList)) {
            extra.setAtUidList(atUidList);
        }
        // 识别消息是否包含连接，插入连接相关消息
        Map<String, UrlInfo> urlContentMap = prioritizedUrlDiscover.getUrlContentMap(update.getContent());
        extra.setUrlContentMap(urlContentMap);
        update.setExtra(extra);
        messageDao.updateById(update);
    }

    @Override
    protected void checkMsg(TextMsgReq body, Long roomId, Long uid) {
        // 校验@的列表
        if (!CollectionUtils.isEmpty(body.getAtUidList())) {
            List<Long> atList = body.getAtUidList().stream().distinct().collect(Collectors.toList());
            if (atList.contains(0L)) {
                Assert.assertTrue("只有管理员才能@全员", roomcommonApi.hasRoomPower(uid, roomId).getCheckData());
                atList = Collections.singletonList(0L);
            } else {
                if (!roomcommonApi.getRoomInfo(roomId).getCheckData().isHotRoom()) {
                    // 确保at的成员都在群聊中
                    List<Long> memberUidList = roomMemberCommonApi.getMemberList(roomId).getCheckData();
                    Assert.assertTrue("@的用户已不在群聊", new HashSet<>(memberUidList).containsAll(atList));
                }
            }
            body.setAtUidList(atList);
        }
        // 校验回复的id
        if (body.getReplyMsgId() != null) {
            Message replyMsg = messageDao.getById(body.getReplyMsgId());
            Assert.assertNotNull("回复消息不存在", replyMsg);
            Assert.assertEquals("只能回复处于同一会话的消息", replyMsg.getRoomId(), roomId);
        }
        // 过滤消息中的敏感词
        body.setContent(sensitiveWords.filter(body.getContent()));
    }

    @Override
    public String messageText(Message message) {
        return message.getContent();
    }

    @Override
    public String replyMsgText(Message message) {
        return message.getContent();
    }

    @Override
    public Object buildMessageBody(Message message) {
        TextMsgResp textMsgResp = new TextMsgResp();
        textMsgResp.setContent(message.getContent());
        textMsgResp.setUrlContentMap(message.getExtra().getUrlContentMap());
        textMsgResp.setAtUidList(message.getExtra().getAtUidList());
        Message reply = messageDao.getById(message.getReplyMsgId());
        if (Objects.nonNull(reply)) {
            TextMsgResp.ReplyMsg replyMsg = new TextMsgResp.ReplyMsg();
            replyMsg.setId(message.getReplyMsgId());
            replyMsg.setUid(reply.getFromUid());
            UserInfoDTO userInfo = userInfoCommonApi.getUserInfo(reply.getFromUid()).getCheckData();
            replyMsg.setUsername(Optional.ofNullable(userInfo).map(UserInfoDTO::getName).orElse(null));
            replyMsg.setType(reply.getType());
            replyMsg.setBody(MsgHandlerFactory.getStrategyNotNull(reply.getType()).replyMsgText(reply));
            replyMsg.setCanCallback(YesOrNoEnum.toStatus(Objects.nonNull(message.getGapCount()) && message.getGapCount() <= CommonConstants.CAN_CALLBACK_GAP_MAX_COUNT));
            replyMsg.setGapCount(message.getGapCount());
            textMsgResp.setReply(replyMsg);
        }
        return textMsgResp;
    }
}
