package com.meteor.chat.user.domain.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class IpResultDTO<T> {

    private String msg;
    private Integer code;
    private T data;

    public boolean isSuccess() {
        return Objects.nonNull(code) && code == 0;
    }
}
