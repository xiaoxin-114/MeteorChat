package com.meteor.chat.frequency.core.strategy;

import com.meteor.chat.common.core.SupplierThrow;
import com.meteor.chat.common.exception.CommonErrorEnum;
import com.meteor.chat.common.exception.FrequencyException;
import com.meteor.chat.frequency.core.dto.FrequencyControlBaseDTO;
import com.meteor.chat.frequency.core.factory.FrequencyControlStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import javax.annotation.PostConstruct;
import java.util.List;
@Slf4j
public abstract class AbstractFrequencyControlStrategy<T extends FrequencyControlBaseDTO> {

    @PostConstruct
    public void registerIntoFactory() {
        FrequencyControlStrategyFactory.registerStrategy(getName(), this);
    }

    public Object processWithFrequencyControl(List<T> frequencyDTOList, SupplierThrow supplier) throws Throwable {
        boolean anyMatch = frequencyDTOList.stream().anyMatch(dto -> StringUtils.isEmpty(dto.getKey()));
        Assert.assertFalse("频控注解的key不能为空", anyMatch);
        if (exceedControlCount(frequencyDTOList)) {
            throw new FrequencyException(CommonErrorEnum.FREQUENCY_LIMIT);
        }
        try {
            return supplier.get();
        }finally {
            incrCount(frequencyDTOList);
        }
    }

    abstract boolean exceedControlCount(List<T> list);

    abstract void incrCount(List<T> list);

    abstract String getName();
}
