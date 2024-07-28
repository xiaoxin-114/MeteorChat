package com.meteor.chat.msg.service.handler;
import com.meteor.chat.chat.service.RoomService;
import com.meteor.chat.chat.service.cache.GroupMemberCache;
import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.common.domain.dto.msg.TextMsgResp.ReplyMsg;
import com.meteor.chat.common.domain.dto.msg.TextMsgReq;
import com.meteor.chat.common.domain.dto.msg.TextMsgResp;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.MessageExtra;
import com.meteor.chat.common.domain.entity.UrlInfo;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.common.domain.enums.MessageTypeEnum;
import com.meteor.chat.common.domain.enums.YesOrNoEnum;
import com.meteor.chat.common.util.discover.PrioritizedUrlDiscover;
import com.meteor.chat.msg.dao.MessageDao;
import com.meteor.chat.user.service.cache.UserCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.stereotype.Component;

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
    private UserCache userCache;

    @Resource
    private GroupMemberCache groupMemberCache;

    @Resource
    private RoomService roomService;

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
        if (CollectionUtils.isNotEmpty(atUidList)) {
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
        if (CollectionUtils.isNotEmpty(body.getAtUidList())) {
            List<Long> atList = body.getAtUidList().stream().distinct().collect(Collectors.toList());
            if (atList.contains(0)) {
                Assert.assertTrue("只有管理员才能@全员", roomService.hasRoomPower(uid, roomId));
                atList = Collections.singletonList(0L);
            } else {
                // 确保at的成员都在群聊中
                List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
                Assert.assertTrue("@的用户已不在群聊", atList.stream().allMatch(id -> memberUidList.contains(id)));
            }
            body.setAtUidList(atList);
        }
        // 校验回复的id
        if (body.getReplyMsgId() != null) {
            Message replyMsg = messageDao.getById(body.getReplyMsgId());
            Assert.assertNotNull("回复消息不存在", replyMsg);
            Assert.assertTrue("只能回复处于同一会话的消息", Objects.equals(replyMsg.getRoomId(), roomId));
        }
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
            ReplyMsg replyMsg = new ReplyMsg();
            replyMsg.setId(message.getReplyMsgId());
            replyMsg.setUid(reply.getFromUid());
            User userInfo = userCache.getUserInfo(reply.getFromUid());
            replyMsg.setUsername(Optional.ofNullable(userInfo).map(User::getName).orElse(null));
            replyMsg.setType(reply.getType());
            replyMsg.setBody(MsgHandlerFactory.getStrategyNotNull(reply.getType()).replyMsgText(reply));
            replyMsg.setCanCallback(YesOrNoEnum.toStatus(Objects.nonNull(message.getGapCount()) && message.getGapCount() <= CommonConstants.CAN_CALLBACK_GAP_MAX_COUNT));
            replyMsg.setGapCount(message.getGapCount());
            textMsgResp.setReply(replyMsg);
        }
        return textMsgResp;
    }
}
