package com.meteor.chat.common.util;

import com.meteor.chat.common.frequency.strategy.AbstractFrequencyControlStrategy;
import com.meteor.chat.common.frequency.dto.FrequencyControlBaseDTO;
import com.meteor.chat.common.frequency.factory.FrequencyControlStrategyFactory;
import org.junit.Assert;

import java.util.List;

public class FrequencyControlUtils {

    public static Object handle(List<FrequencyControlBaseDTO> list, SupplierThrow supplier, String strategyName) throws Throwable {
        AbstractFrequencyControlStrategy strategy = FrequencyControlStrategyFactory.getStrategy(strategyName);
        Assert.assertNotNull("频控策略未注册", strategy);
        return strategy.processWithFrequencyControl(list, supplier);
    }
}
