package com.meteor.chat.msg.urldiscover;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.meteor.chat.common.utils.FutureUtils;
import com.meteor.chat.msg.config.MsgConfiguration;
import com.meteor.chat.msg.domain.entity.UrlInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public abstract class AbstractUrlDiscover implements UrlDiscover {

    @Qualifier(value = MsgConfiguration.EXECUTOR)
    @Autowired
    private Executor executor;

    private static final Pattern URL_PATTERN = Pattern.compile("((http|https)://)?(www.)?([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?");

    @Override
    public Map<String, UrlInfo> getUrlContentMap(String content) {
        if (StringUtils.isEmpty(content)) {
            return new HashMap<>();
        }
        Matcher matcher = URL_PATTERN.matcher(content);
        List<String> urlList = new ArrayList<>();
        // 解析文字信息中的每个url
        while (matcher.find()) {
            String url = matcher.group();
            urlList.add(url);
        }
        List<CompletableFuture<Pair<String, UrlInfo>>> completableFutureList = urlList.stream().map(url -> CompletableFuture.supplyAsync(() -> Pair.of(url, getContent(url)), executor))
                .collect(Collectors.toList());
        CompletableFuture<List<Pair<String, UrlInfo>>> mapFuture = FutureUtils.sequenceNonNull(completableFutureList);
        return mapFuture.join().stream().collect(Collectors.toMap(pair -> pair.getKey(),pair -> pair.getValue()));
    }

    @Override
    public UrlInfo getContent(String url) {
        Connection connect;
        try {
            SslUtils.ignoreSsl();
            connect = Jsoup.connect(assemble(url));
            connect.timeout(2000);
            Document document = connect.get();
            if (Objects.isNull(document)) {
                return null;
            }
            return UrlInfo.builder()
                    .title(getTitle(document))
                    .description(getDescription(document))
                    .image(getImage(assemble(url), document))
                    .build();
        }catch (Exception e) {
            log.error("parse url failed url:{" + url + "}", e);
        }
        return null;
    }

    /**
     * 判断链接是否有效
     * 输入链接
     * 返回true或者false
     */
    public static boolean isConnect(String href) {
        //请求地址
        URL url;
        //请求状态码
        int state;
        //下载链接类型
        String fileType;
        try {
            url = new URL(href);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            state = httpURLConnection.getResponseCode();
            fileType = httpURLConnection.getHeaderField("Content-Disposition");
            //如果成功200，缓存304，移动302都算有效链接，并且不是下载链接
            if ((state == 200 || state == 302 || state == 304) && fileType == null) {
                return true;
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static String assemble(String url) {

        if (!StrUtil.startWith(url, "http")) {
            return "http://" + url;
        }

        return url;
    }
}
