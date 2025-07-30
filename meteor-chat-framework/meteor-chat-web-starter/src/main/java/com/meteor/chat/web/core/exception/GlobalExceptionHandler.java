package com.meteor.chat.web.core.exception;

import com.meteor.chat.common.exception.BusinessException;
import com.meteor.chat.common.exception.CommonErrorEnum;
import com.meteor.chat.common.exception.FrequencyException;
import com.meteor.chat.common.result.ApiResult;
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
    public ApiResult<Void> argsNotValidExceptionHandler(BindException e){
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fieldError ->
                errorMsg.append(fieldError.getField()).append(fieldError.getDefaultMessage()).append(","));
        log.info("validation parameters error！The reason is:{}", errorMsg.toString());
        return ApiResult.fail(CommonErrorEnum.PARAM_VALID.getErrCode(), errorMsg.toString());
    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiResult<Void> businessExceptionHandler(BusinessException e) {
        log.error(e.getMessage(), e);
        return ApiResult.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = AssertionError.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ApiResult<Void> assertErrorHandler(AssertionError error) {
        log.error(error.getMessage(), error);
        return ApiResult.fail(CommonErrorEnum.PARAM_ERROR.getErrCode(), error.getMessage());
    }

    @ExceptionHandler(value = FrequencyException.class)
    @ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
    public ApiResult<Void> frequencyExceptionHandler(FrequencyException exception) {
        log.error(exception.getMessage(), exception);
        return ApiResult.fail(CommonErrorEnum.FREQUENCY_LIMIT.getErrCode(), exception.getMessage());
    }

    @ExceptionHandler(value = Throwable.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Void> exceptionHandler(Throwable e) {
        log.error(e.getMessage(), e);
        return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR.getErrCode(), CommonErrorEnum.SYSTEM_ERROR.getErrMsg());
    }
}
