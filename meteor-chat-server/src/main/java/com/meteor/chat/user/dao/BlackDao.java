package com.meteor.chat.user.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meteor.chat.common.domain.entity.Black;
import com.meteor.chat.common.domain.enums.BlackTypeEnum;
import com.meteor.chat.common.mapper.BlackMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BlackDao extends ServiceImpl<BlackMapper, Black> {

    public void blackUid(Long uid) {
        Black black = new Black();
        black.setTarget(uid.toString());
        black.setType(BlackTypeEnum.UID.getId());
        save(black);
    }

    public void blackIP(String ip) {
        Black black = new Black();
        black.setTarget(ip);
        black.setType(BlackTypeEnum.IP.getId());
        save(black);
    }

}
