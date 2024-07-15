package com.meteor.chat.common.domain.entity;

import lombok.*;

import java.io.Serializable;

/**
 * 链接网站的基础信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UrlInfo implements Serializable {
    /**
     * 网站标题
     */
    private String title;
    /**
     * 网站概述
     */
    private String description;
    /**
     * 网站图标
     */
    private String image;

}
