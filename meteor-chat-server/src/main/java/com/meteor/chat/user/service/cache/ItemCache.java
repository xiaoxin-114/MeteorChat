package com.meteor.chat.user.service.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meteor.chat.common.domain.entity.ItemConfig;
import com.meteor.chat.common.mapper.ItemConfigMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ItemCache {

    @Resource
    private ItemConfigMapper itemConfigMapper;

    @Cacheable(cacheNames = "item", key = "'itemsByType:' + #type")
    public ItemConfig getByType(String type) {
        LambdaQueryWrapper<ItemConfig> queryWrapper = new LambdaQueryWrapper<ItemConfig>().eq(ItemConfig::getType, type);
        return itemConfigMapper.selectOne(queryWrapper);
    }

    @Cacheable(cacheNames = "item", key = "'item:' + #id")
    public ItemConfig getById(Long id) {
        return itemConfigMapper.selectById(id);
    }
}
