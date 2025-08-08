package com.meteor.chat.sensitiveword.core.api;

import com.meteor.chat.api.user.constants.ApiConstants;
import com.meteor.chat.common.constants.RpcConstants;
import com.meteor.chat.sensitiveword.core.domain.SensitiveWord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface SensitiveWordApi {

    List<SensitiveWord> listSensitiveWord();
}
