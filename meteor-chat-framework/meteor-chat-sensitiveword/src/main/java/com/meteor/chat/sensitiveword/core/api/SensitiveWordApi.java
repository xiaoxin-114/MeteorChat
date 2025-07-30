package com.meteor.chat.sensitiveword.core.api;

import com.meteor.chat.api.user.constants.ApiConstants;
import com.meteor.chat.common.constants.RpcConstants;
import com.meteor.chat.sensitiveword.core.domain.SensitiveWord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
@FeignClient(name = RpcConstants.USER_SERVER_NAME)
public interface SensitiveWordApi {
    @GetMapping(ApiConstants.SENSITIVE_WORD_PREFIX)
    List<SensitiveWord> listSensitiveWord();
}
