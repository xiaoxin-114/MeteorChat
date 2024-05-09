package com.meteor.chat.user.service.cache;

import com.meteor.chat.common.cache.AbstractRedisStringCache;
import com.meteor.chat.common.constants.RedisKey;
import com.meteor.chat.common.domain.dto.SummaryInfoDTO;
import com.meteor.chat.common.domain.entity.*;
import com.meteor.chat.common.domain.enums.ItemConfigTypeEnum;
import com.meteor.chat.common.mapper.ItemConfigMapper;
import com.meteor.chat.user.dao.ItemConfigDao;
import com.meteor.chat.user.service.UserBackpackService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserSummaryCache extends AbstractRedisStringCache<Long, SummaryInfoDTO> {

    @Resource
    private UserInfoCache userInfoCache;
    @Resource
    private ItemConfigDao itemConfigDao;
    @Resource
    private UserBackpackService userBackpackService;

    @Override
    protected Long getExpireTime() {
        return 10 * 60L;
    }

    @Override
    public String getKey(Long aLong) {
        return RedisKey.getKey(RedisKey.USER_SUMMARY_STRING, aLong);
    }

    @Override
    public Map<Long, SummaryInfoDTO> load(List<Long> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Map<Long, User> userMap = userInfoCache.getBatch(list);
        List<ItemConfig> itemConfigs = itemConfigDao.listByType(ItemConfigTypeEnum.BADGE.getType());
        List<Long> itemIdList = itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList());
        List<UserBackpack> userBackpacks = userBackpackService.listByUidAndItemId(list, itemIdList);
        Map<Long, List<UserBackpack>>  userBadgeMap = userBackpacks.stream()
                    .collect(Collectors.groupingBy(UserBackpack::getUid));
        return list.stream().map(uid -> {
            SummaryInfoDTO infoDTO = new SummaryInfoDTO();
            User user = userMap.get(uid);
            if (user == null) {
                return null;
            }
            List<UserBackpack> backpacks = userBadgeMap.get(uid);
            infoDTO.setUid(uid);
            infoDTO.setAvatar(user.getAvatar());
            infoDTO.setName(user.getName());
            infoDTO.setWearingItemId(user.getItemId());
            infoDTO.setLocPlace(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIpDetail).map(IpDetail::getCity).orElse(null));
            if (!CollectionUtils.isEmpty(backpacks)) {
                infoDTO.setItemIds(backpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toList()));
            }
            return infoDTO;
        }).filter(Objects::nonNull).collect(Collectors.toMap(SummaryInfoDTO::getUid, Function.identity()));
    }
}
