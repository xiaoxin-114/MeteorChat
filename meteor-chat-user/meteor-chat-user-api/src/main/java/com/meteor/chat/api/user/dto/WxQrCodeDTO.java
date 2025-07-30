package com.meteor.chat.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxQrCodeDTO {
    private String qrCodeUrl;
}
