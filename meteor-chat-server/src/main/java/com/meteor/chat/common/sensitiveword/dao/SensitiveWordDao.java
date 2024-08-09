package com.meteor.chat.common.sensitiveword.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.SensitiveWord;
import com.meteor.chat.common.mapper.SensitiveWordMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SensitiveWordDao extends ServiceImpl<SensitiveWordMapper, SensitiveWord> {
}
