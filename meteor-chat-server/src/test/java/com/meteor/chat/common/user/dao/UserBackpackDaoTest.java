package com.meteor.chat.common.user.dao;

import com.meteor.chat.MeteorChatApplication;
import com.meteor.chat.user.dao.UserBackpackDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = MeteorChatApplication.class)
@RunWith(SpringRunner.class)
public class UserBackpackDaoTest {
    @Resource
    private UserBackpackDao userBackpackDao;

    @Test
    public void testUseItem() {
        System.out.println(userBackpackDao.useOne(4L));
    }
}
