package com.meteor.chat.websocket.domain.vo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WSAuthorize {
    private String token;
}
