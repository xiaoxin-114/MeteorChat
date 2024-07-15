package com.meteor.chat.common.util.discover;

import com.meteor.chat.common.domain.entity.UrlInfo;
import org.jsoup.nodes.Document;

import java.util.Map;

public interface UrlDiscover {
    Map<String, UrlInfo> getUrlContentMap(String content);
    UrlInfo getContent(String url);
    String getImage(String url, Document document);
    String getTitle(Document document);
    String getDescription(Document document);
}
