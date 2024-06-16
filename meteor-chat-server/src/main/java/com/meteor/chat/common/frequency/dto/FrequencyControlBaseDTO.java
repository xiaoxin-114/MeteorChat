package com.meteor.chat.common.frequency.dto;

import lombok.Data;

import java.util.concurrent.TimeUnit;
@Data
public class FrequencyControlBaseDTO {
    private String key;

    private Integer count;

    private Integer time;

    private TimeUnit unit;
}
