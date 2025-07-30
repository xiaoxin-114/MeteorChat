package com.meteor.chat.frequency.core.utils;

import com.meteor.chat.common.core.SupplierThrow;
import com.meteor.chat.frequency.core.dto.FrequencyControlBaseDTO;
import com.meteor.chat.frequency.core.factory.FrequencyControlStrategyFactory;
import com.meteor.chat.frequency.core.strategy.AbstractFrequencyControlStrategy;
import org.junit.Assert;

import java.util.List;

public class FrequencyControlUtils {

    public static Object handle(List<FrequencyControlBaseDTO> list, SupplierThrow supplier, String strategyName) throws Throwable {
        AbstractFrequencyControlStrategy strategy = FrequencyControlStrategyFactory.getStrategy(strategyName);
        Assert.assertNotNull("频控策略未注册", strategy);
        return strategy.processWithFrequencyControl(list, supplier);
    }
}
