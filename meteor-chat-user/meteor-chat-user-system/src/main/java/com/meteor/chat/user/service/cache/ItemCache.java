package com.meteor.chat.user.service.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meteor.chat.user.domain.entity.ItemConfig;
import com.meteor.chat.user.enums.ItemConfigTypeEnum;
import com.meteor.chat.user.mapper.ItemConfigMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ItemCache {

    @Resource
    private ItemConfigMapper itemConfigMapper;

    @Cacheable(cacheNames = "item", key = "'itemsByType:' + #type")
    public List<ItemConfig> getByType(String type) {
        LambdaQueryWrapper<ItemConfig> queryWrapper = new LambdaQueryWrapper<ItemConfig>().eq(ItemConfig::getType, type);
        return itemConfigMapper.selectList(queryWrapper);
    }

    @Cacheable(cacheNames = "item", key = "'item:' + #id")
    public ItemConfig getById(Long id) {
        return itemConfigMapper.selectById(id);
    }

    public Long getRenameCardId() {
        return getByType(ItemConfigTypeEnum.MODIFY_NAME_CARD.getType().toString()).get(0).getId();
    }
}
