package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.WxMsg;
import com.meteor.chat.common.mapper.WxMsgMapper;
import org.springframework.stereotype.Repository;

@Repository
public class WxMsgDao extends ServiceImpl<WxMsgMapper, WxMsg> {

}
