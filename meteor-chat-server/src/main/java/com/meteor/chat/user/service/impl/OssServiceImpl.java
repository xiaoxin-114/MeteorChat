package com.meteor.chat.user.service.impl;

import com.meteor.chat.common.domain.enums.FileUploadSceneEnum;
import com.meteor.chat.common.domain.vo.req.UploadUrlReq;
import com.meteor.chat.oss.MinIOTemplate;
import com.meteor.chat.oss.domain.OssReq;
import com.meteor.chat.oss.domain.OssResp;
import com.meteor.chat.user.service.OssService;
import org.junit.Assert;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class OssServiceImpl implements OssService {

    @Resource
    private MinIOTemplate minIOTemplate;

    @Override
    public OssResp uploadUrl(UploadUrlReq req, Long uid) {
        FileUploadSceneEnum sceneEnum = FileUploadSceneEnum.of(req.getScene());
        Assert.assertNotNull("文件场景异常", sceneEnum);
        OssReq ossReq = OssReq.builder()
                .fileName(req.getFileName())
                .filePath(sceneEnum.getPath())
                .uid(uid)
                .build();
        return minIOTemplate.getPreSignedObjectUrl(ossReq);
    }
}
