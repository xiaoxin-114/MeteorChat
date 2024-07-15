package com.meteor.chat.common.util.discover;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommonUrlDiscover extends AbstractUrlDiscover{

    @Override
    public String getImage(String url, Document document) {
        String image = document.select("link[type=image/x-icon]").attr("href");
        //如果没有去匹配含有icon属性的logo
        String href = StrUtil.isEmpty(image) ? document.select("link[rel$=icon]").attr("href") : image;
        //如果url已经包含了logo
        if (StrUtil.containsAny(url, "favicon")) {
            return url;
        }
        //如果icon可以直接访问或者包含了http
        if (isConnect(!StrUtil.startWith(href, "http") ? "http:" + href : href)) {
            return href;
        }

        return StrUtil.format("{}/{}", url, StrUtil.removePrefix(href, "/"));
    }


    @Override
    public String getTitle(Document document) {
        return document.title();
    }

    @Override
    public String getDescription(Document document) {
        String description = document.head().select("meta[name=description]").attr("content");
        String keywords = document.head().select("meta[name=keywords]").attr("content");
        String content = StrUtil.isNotBlank(description) ? description : keywords;
        //只保留一句话的描述
        return StrUtil.isNotBlank(content) ? content.substring(0, content.indexOf("。")) : content;
    }
}
