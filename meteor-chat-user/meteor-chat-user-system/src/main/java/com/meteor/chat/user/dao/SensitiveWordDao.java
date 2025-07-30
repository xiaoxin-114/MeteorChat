package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.sensitiveword.core.domain.SensitiveWord;
import com.meteor.chat.user.mapper.SensitiveWordMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SensitiveWordDao extends ServiceImpl<SensitiveWordMapper, SensitiveWord> {
}
