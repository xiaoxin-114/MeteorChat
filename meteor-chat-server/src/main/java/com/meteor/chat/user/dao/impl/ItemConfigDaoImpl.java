package com.meteor.chat.user.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.ItemConfig;
import com.meteor.chat.common.mapper.ItemConfigMapper;
import com.meteor.chat.user.dao.ItemConfigDao;
import io.minio.messages.Item;

import java.util.List;

public class ItemConfigDaoImpl extends ServiceImpl<ItemConfigMapper, ItemConfig> implements ItemConfigDao {
    @Override
    public List<ItemConfig> listByType(Integer type) {
        LambdaQueryWrapper<ItemConfig> queryWrapper = new LambdaQueryWrapper<ItemConfig>()
                .eq(ItemConfig::getType, type);
        return list(queryWrapper);
    }
}
