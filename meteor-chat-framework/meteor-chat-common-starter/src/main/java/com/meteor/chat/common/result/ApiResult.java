package com.meteor.chat.common.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.common.exception.ErrorEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "基础api响应体")
public class ApiResult<T> implements Serializable {
    @Schema(description = "成功标识true or false")
    private Boolean success;
    @Schema(description = "错误码")
    private Integer errCode;
    @Schema(description = "错误消息")
    private String errMsg;
    @Schema(description = "返回数据对象")
    private T data;

    public static <T> ApiResult<T> success(){
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(Boolean.TRUE);
        return result;
    }

    public static <T> ApiResult<T> success(T data){
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(Boolean.TRUE);
        result.setData(data);
        return result;
    }

    public static <T> ApiResult<T> fail(Integer errCode, String errMsg){
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(Boolean.FALSE);
        result.setErrCode(errCode);
        result.setErrMsg(errMsg);
        return result;
    }

    public static <T> ApiResult<T> fail(ErrorEnum errorEnum){
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(Boolean.FALSE);
        result.setErrCode(errorEnum.getErrCode());
        result.setErrMsg(errorEnum.getErrMsg());
        return result;
    }

    public boolean isSuccess(){
        return success;
    }

    @JsonIgnore
    public T getCheckData() {
        checkError();
        return data;
    }

    private void checkError() {
        if (!success) {
            throw new BusinessException(errCode, errMsg);
        }
    }
}
