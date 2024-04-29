package com.meteor.chat.common.exception.handler;

import com.meteor.chat.common.domain.result.ApiResult;
import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.common.exception.CommonErrorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiResult argsNotValidExceptionHandler(BindException e){
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError ->
                errorMsg.append(fieldError.getField()).append(fieldError.getDefaultMessage()).append(","));
        log.info("validation parameters error！The reason is:{}", errorMsg.toString());
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrCode(), errorMsg.toString());
    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiResult businessExceptionHandler(BusinessException e) {
        log.error(e.getMessage(), e);
        return ApiResult.fail(e.getCode(), e.getMessage());
    }
}
