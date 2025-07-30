package com.meteor.chat.common.utils;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Arrays;

public class SpElUtils {
    private static final SpelExpressionParser parser = new SpelExpressionParser();
    private static final DefaultParameterNameDiscoverer paramNameDiscover = new DefaultParameterNameDiscoverer();

    public static String parseSpEl(Method method, String spEl, Object... args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 获取方法参数的名称数组
        String[] parameterNames = paramNameDiscover.getParameterNames(method);
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        // 把参数与参数名一一对应放入解析器上下文中
        Expression expression = parser.parseExpression(spEl);
        // 对sqEL表达式进行解析
        return expression.getValue(context, String.class);
    }

    public static void main(String[] args) throws NoSuchMethodException {
        Method method = SpElUtils.class.getDeclaredMethod("parseSpEl", Method.class, String.class, Object[].class);
        String[] parameterNames = paramNameDiscover.getParameterNames(method);
        System.out.println(Arrays.toString(parameterNames));
    }
}
