package com.meteor.chat.user.service.cache;

import com.meteor.chat.common.cache.AbstractRedisStringCache;
import com.meteor.chat.common.constants.RedisKey;
import com.meteor.chat.common.domain.entity.User;
import com.meteor.chat.user.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
@Component
@Slf4j
public class UserInfoCache extends AbstractRedisStringCache<Long, User>  {

    @Resource
    private UserDao userDao;

    @Override
    protected Long getExpireTime() {
        return 5 * 60L;
    }

    @Override
    public String getKey(Long aLong) {
        return RedisKey.getKey(RedisKey.USER_INFO_STRING, aLong);
    }

    @Override
    public Map<Long, User> load(List<Long> list) {
        return Optional.ofNullable(userDao.listByIds(list).stream().collect(Collectors.toMap(User::getId, Function.identity()))).orElse(null);
    }
}
