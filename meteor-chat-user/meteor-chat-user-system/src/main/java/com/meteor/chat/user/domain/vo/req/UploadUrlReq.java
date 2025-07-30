package com.meteor.chat.user.domain.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("获取文件临时上传链接请求对象")
public class UploadUrlReq {
    @ApiModelProperty(value = "文件名（带后缀）")
    @NotBlank
    private String fileName;
    @ApiModelProperty(value = "上传场景1.聊天室,2.表情包")
    @NotNull
    /**
     * @see com.meteor.chat.common.domain.enums.FileUploadSceneEnum
     */
    private Integer scene;
}
