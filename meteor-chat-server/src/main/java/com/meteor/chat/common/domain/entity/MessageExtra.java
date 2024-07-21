package com.meteor.chat.common.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.meteor.chat.common.domain.dto.msg.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageExtra implements Serializable {

    //url跳转链接
    private Map<String, UrlInfo> urlContentMap;
    //消息撤回详情
    private MsgRecall recall;
    //艾特的uid
    private List<Long> atUidList;

    private FileMsgDTO fileMsgDTO;

    private EmojisMsgDTO emojisMsgDTO;

    private ImgMsgDTO imgMsgDTO;

    private SoundMsgDTO soundMsgDTO;

    private TextMsgDTO textMsgDTO;

    private VideoMsgDTO videoMsgDTO;
}
