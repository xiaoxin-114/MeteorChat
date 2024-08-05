package com.meteor.chat.user.service;

import com.meteor.chat.common.domain.vo.req.UploadUrlReq;
import com.meteor.chat.oss.domain.OssResp;

public interface OssService {

    OssResp uploadUrl(UploadUrlReq req, Long uid);
}
