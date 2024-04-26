package com.meteor.chat.common.domain.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class IpInfo implements Serializable {
    private String createIp;
    private IpDetail createIpDetail;
    private String updateIp;
    private IpDetail updateIpDetail;
}
