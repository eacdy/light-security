package com.itmuch.lightsecurity.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author itmuch.com
 */
@UtilityClass
@Slf4j
public class SpringElCheckUtil {
    private static ExpressionParser PARSER = new SpelExpressionParser();

    /**
     * 校验expression是否能通过rootObject的检测
     *
     * @param rootObject rootObject
     * @param expression 表达式
     * @return 是否通过
     */
    public static boolean check(Object rootObject, String expression) {
        EvaluationContext context = new StandardEvaluationContext(rootObject);
        Boolean value = PARSER.parseExpression(expression)
                .getValue(context, Boolean.class);
        log.info("rootObject = {}, expression = {}", rootObject, expression);
        return value != null ? value : false;
    }
}
