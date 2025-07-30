package com.meteor.chat.frequency.core.factory;


import com.meteor.chat.frequency.core.strategy.AbstractFrequencyControlStrategy;

import java.util.HashMap;
import java.util.Map;

public class FrequencyControlStrategyFactory {
    private final static Map<String, AbstractFrequencyControlStrategy> STRATEGY_MAP = new HashMap<>();

    public static void registerStrategy(String name, AbstractFrequencyControlStrategy strategy) {
        STRATEGY_MAP.put(name, strategy);
    }

    public static AbstractFrequencyControlStrategy getStrategy(String name) {
        return STRATEGY_MAP.get(name);
    }
}
