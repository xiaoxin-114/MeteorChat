package com.meteor.chat.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxQrCodeDTO implements Serializable {
    private String qrCodeUrl;
}
