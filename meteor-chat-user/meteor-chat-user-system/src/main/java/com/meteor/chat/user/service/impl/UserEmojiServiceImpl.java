package com.meteor.chat.user.service.impl;

import com.meteor.chat.common.constants.CommonConstants;
import com.meteor.chat.user.dao.UserEmojiDao;
import com.meteor.chat.user.domain.entity.UserEmoji;
import com.meteor.chat.user.domain.vo.IdRespVO;
import com.meteor.chat.user.domain.vo.UserEmojiResp;
import com.meteor.chat.user.domain.vo.req.IdBaseReq;
import com.meteor.chat.user.domain.vo.req.UserEmojiAddReq;
import com.meteor.chat.user.service.UserEmojiService;
import org.junit.Assert;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserEmojiServiceImpl implements UserEmojiService {

    @Resource
    private UserEmojiDao userEmojiDao;

    @Override
    public List<UserEmojiResp> listEmoji(Long uid) {
        List<UserEmoji> list = userEmojiDao.listByUid(uid);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(UserEmoji::toResp).collect(Collectors.toList());
    }

    @Override
    public IdRespVO addEmoji(UserEmojiAddReq req, Long uid) {
        long count = userEmojiDao.countByUid(uid);
        Assert.assertTrue("最多支持收藏" + CommonConstants.USER_EMOJI_MAX_NUM + "个表情包", count < CommonConstants.USER_EMOJI_MAX_NUM);
        List<UserEmoji> emojiList = userEmojiDao.getByUidAndUrl(uid, req.getExpressionUrl());
        Assert.assertTrue("该表情包已经收藏过了~", CollectionUtils.isEmpty(emojiList));
        UserEmoji userEmoji = new UserEmoji();
        userEmoji.setUid(uid);
        userEmoji.setExpressionUrl(req.getExpressionUrl());
        userEmojiDao.save(userEmoji);
        return IdRespVO.id(userEmoji.getId());
    }

    @Override
    public void removeEmoji(IdBaseReq req, Long uid) {
        UserEmoji emoji = userEmojiDao.getById(req.getId());
        Assert.assertNotNull("表情包不存在", emoji);
        Assert.assertEquals("不能删除别人的表情包", emoji.getUid(), uid);
        userEmojiDao.removeById(req.getId());
    }
}
