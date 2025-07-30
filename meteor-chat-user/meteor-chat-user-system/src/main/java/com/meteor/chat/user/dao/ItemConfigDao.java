package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.user.domain.entity.ItemConfig;
import com.meteor.chat.user.mapper.ItemConfigMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> {
    public List<ItemConfig> listByType(Integer type) {
        LambdaQueryWrapper<ItemConfig> queryWrapper = new LambdaQueryWrapper<ItemConfig>()
                .eq(ItemConfig::getType, type);
        return list(queryWrapper);
    }
}
