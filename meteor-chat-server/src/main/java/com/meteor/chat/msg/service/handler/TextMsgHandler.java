package com.meteor.chat.msg.service.handler;

import com.meteor.chat.common.domain.dto.msg.TextMsgDTO;
import com.meteor.chat.common.domain.dto.msg.VideoMsgDTO;
import com.meteor.chat.common.domain.entity.Message;
import com.meteor.chat.common.domain.entity.MessageExtra;
import com.meteor.chat.common.domain.entity.UrlInfo;
import com.meteor.chat.common.domain.enums.MessageTypeEnum;
import com.meteor.chat.common.util.discover.PrioritizedUrlDiscover;
import com.meteor.chat.msg.dao.MessageDao;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgDTO> {
    @Resource
    private MessageDao messageDao;

    @Resource
    private PrioritizedUrlDiscover prioritizedUrlDiscover;

    private final MessageTypeEnum MESSAGE_TYPE = MessageTypeEnum.TEXT;

    @Override
    MessageTypeEnum getMsgType() {
        return MESSAGE_TYPE;
    }

    @Override
    void saveMessageExtra(Message message, TextMsgDTO body) {
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
        MessageExtra messageExtra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        messageExtra.setUrlContentMap(urlContentMap);
        update.setExtra(messageExtra);
        messageDao.updateById(update);
    }

    @Override
    public String messageText(Message message) {
        return message.getContent();
    }
}
