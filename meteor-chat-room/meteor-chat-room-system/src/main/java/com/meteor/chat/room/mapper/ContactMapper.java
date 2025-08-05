package com.meteor.chat.room.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meteor.chat.room.domain.entity.Contact;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author meteor
* @description 针对表【contact(会话列表)】的数据库操作Mapper
* @createDate 2024-03-29 17:21:14
* @Entity generator.domain.Contact
*/
public interface ContactMapper extends BaseMapper<Contact> {
    void refreshActiveTime(@Param("roomId") Long roomId,
                           @Param("uidList") List<Long> uidList,
                           @Param("sendTime") Date sendTime,
                           @Param("msgId") Long msgId);
}




