package com.meteor.chat.msg.service.handler;
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
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgReq> {
    @Resource
    private MessageDao messageDao;

    @Resource
    private PrioritizedUrlDiscover prioritizedUrlDiscover;

    private final MessageTypeEnum MESSAGE_TYPE = MessageTypeEnum.TEXT;

    @Resource
    private UserCache userCache;

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
        Map<String, UrlInfo> urlContentMap = prioritizedUrlDiscover.getUrlContentMap(message.getContent());
        extra.setUrlContentMap(urlContentMap);
        update.setExtra(extra);
        messageDao.updateById(update);
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
