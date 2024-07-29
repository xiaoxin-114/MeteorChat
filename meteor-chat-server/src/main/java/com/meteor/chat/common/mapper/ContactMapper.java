package com.meteor.chat.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meteor.chat.common.domain.entity.Contact;

import java.util.Date;
import java.util.List;

/**
* @author meteor
* @description 针对表【contact(会话列表)】的数据库操作Mapper
* @createDate 2024-03-29 17:21:14
* @Entity generator.domain.Contact
*/
public interface ContactMapper extends BaseMapper<Contact> {
    void refreshActiveTime(Long roomId, List<Long> uidList, Date sendTime, Long msgId);
}




