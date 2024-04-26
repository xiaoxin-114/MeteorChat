package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.meteor.chat.common.domain.entity.ItemConfig;

import java.util.List;

public interface ItemConfigDao extends IService<ItemConfig> {
    List<ItemConfig> listByType(Integer type);
}
