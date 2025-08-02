package com.meteor.chat.web.core.utils;

import cn.hutool.http.ContentType;
import com.meteor.chat.common.result.ApiResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WebFrameworkUtils {

    public static void sendErrorMsg(HttpServletResponse response, ApiResult<Void> result) throws IOException {
        response.setStatus(result.getErrCode());
        response.setContentType(ContentType.JSON.toString(StandardCharsets.UTF_8));
        response.getWriter().write(result.toString());
    }
}
