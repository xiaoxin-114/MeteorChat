package com.meteor.chat;

import com.meteor.chat.common.domain.entity.ItemConfig;
import com.meteor.chat.common.domain.enums.ItemConfigTypeEnum;
import com.meteor.chat.user.dao.ItemConfigDao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MyBatisTest {
    @Resource
    private ItemConfigDao itemConfigDao;

    @Test
    public void getItem() {
        List<ItemConfig> itemConfigs = itemConfigDao.listByType(ItemConfigTypeEnum.MODIFY_NAME_CARD.getType());
        System.out.println(itemConfigs);
    }
}
