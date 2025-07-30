package com.meteor.chat.user.service;

import com.meteor.chat.oss.core.domain.OssResp;
import com.meteor.chat.user.domain.vo.req.UploadUrlReq;

public interface OssService {

    OssResp uploadUrl(UploadUrlReq req, Long uid);
}
