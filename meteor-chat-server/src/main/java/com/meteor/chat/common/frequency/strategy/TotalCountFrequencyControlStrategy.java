package com.meteor.chat.common.frequency.strategy;

import com.meteor.chat.common.frequency.dto.FrequencyControlBaseDTO;
import com.meteor.chat.common.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Component
@Slf4j
public class TotalCountFrequencyControlStrategy extends AbstractFrequencyControlStrategy<FrequencyControlBaseDTO> {
    private static final String NAME = "TotalCountWithInFixTime";

    @Override
    boolean exceedControlCount(List<FrequencyControlBaseDTO> list) {
        List<String> keyLis = list.stream().map(FrequencyControlBaseDTO::getKey).collect(Collectors.toList());
        List<Integer> countList = RedisUtils.mget(keyLis, Integer.class);
        for (int i = 0; i < keyLis.size(); i++) {
            FrequencyControlBaseDTO dto = list.get(i);
            Integer count = countList.get(i);
            if (Objects.nonNull(count) && count >= dto.getCount()) {
                log.warn("key为{}请求超出频控，要求{}{}下{}次，实际{}次，", dto.getKey(), dto.getTime(), dto.getUnit().toString(), dto.getCount(), count);
                return true;
            }
        }
        return false;
    }

    @Override
    void incrCount(List<FrequencyControlBaseDTO> list) {
        list.forEach(dto -> RedisUtils.inc(dto.getKey(), dto.getTime(), dto.getUnit()));
    }

    @Override
    String getName() {
        return NAME;
    }
}
