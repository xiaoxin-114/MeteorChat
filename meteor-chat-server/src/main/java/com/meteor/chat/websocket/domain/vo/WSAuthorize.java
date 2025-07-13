package com.meteor.chat.websocket.domain.vo;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WSAuthorize implements Serializable {
    private String token;
}
