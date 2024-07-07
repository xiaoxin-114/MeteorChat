package com.meteor.chat.transaction.domian;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecureInvokeDTO {
    private String className;
    private String methodName;
    private String parameterTypes;
    private String args;
}
