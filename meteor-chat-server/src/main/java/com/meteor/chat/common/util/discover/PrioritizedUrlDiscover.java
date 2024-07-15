package com.meteor.chat.common.util.discover;

import com.meteor.chat.common.domain.entity.UrlInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
/**
 * 为所有url解析器制定优先级，按照顺序执行
 */
public class PrioritizedUrlDiscover extends AbstractUrlDiscover{
    private final List<UrlDiscover> urlDiscoverList = new ArrayList<>();

    {
        urlDiscoverList.add(new CommonUrlDiscover());
        urlDiscoverList.add(new WxUrlDiscover());
    }

    @Override
    public String getImage(String url, Document document) {
        for (UrlDiscover urlDiscover:
             urlDiscoverList) {
            String image = urlDiscover.getImage(url, document);
            if (StringUtils.isNotEmpty(image)) {
                return image;
            }
        }
        return null;
    }

    @Override
    public String getTitle(Document document) {
        for (UrlDiscover urlDiscover:
                urlDiscoverList) {
            String image = urlDiscover.getTitle(document);
            if (StringUtils.isNotEmpty(image)) {
                return image;
            }
        }
        return null;
    }

    @Override
    public String getDescription(Document document) {
        for (UrlDiscover urlDiscover:
                urlDiscoverList) {
            String image = urlDiscover.getDescription(document);
            if (StringUtils.isNotEmpty(image)) {
                return image;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String url = "https://mp.weixin.qq.com/s/GQGidprakfticYnbVYVYGQ";
        PrioritizedUrlDiscover urlDiscover = new PrioritizedUrlDiscover();
        Map<String, UrlInfo> urlContentMap = urlDiscover.getUrlContentMap(url);
        System.out.println(urlContentMap);
    }
}
