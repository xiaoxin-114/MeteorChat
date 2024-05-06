package com.meteor.chat.common.domain.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Data
public class IpInfo implements Serializable {
    private String createIp;
    private IpDetail createIpDetail;
    // 最新登陆的ip
    private String updateIp;
    // 最新登陆的ip详情信息
    private IpDetail updateIpDetail;

    public void refreshIp(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return;
        }
        updateIp = ip;
        if (createIp == null) {
            createIp = ip;
        }
    }

    /**
     * 判断用户是否需要更新ip，如果需要更新ip，那么一定是updateIP，不可能存在需要更新createIP而不需要更新updateIP的情况
     * createIP只有注册的时候更新，此时updateIP和createIP一致，这样更新后两者的ip详情都会更新
     * @return
     */
    public String needRefreshIp() {
        boolean notNeed = Optional.ofNullable(updateIpDetail)
                .map(IpDetail::getIp)
                .filter(ip -> Objects.equals(ip, updateIp))
                .isPresent();
        return notNeed ? null : updateIp;
    }

    public void refreshIpDetail(IpDetail ipDetail) {
        if (ipDetail == null) {
            return;
        }
        if (ipDetail.getIp().equals(createIp)) {
            this.createIpDetail = ipDetail;
        }
        if (ipDetail.getIp().equals(updateIp)) {
            this.updateIpDetail = ipDetail;
        }
    }
}
