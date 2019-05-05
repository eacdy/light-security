package com.itmuch.lightsecurity.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import reactor.core.publisher.Mono;

/**
 * @author itmuch.com
 */
@UtilityClass
@Slf4j
public class ReactiveSpringElCheckUtil {
    private static ExpressionParser PARSER = new SpelExpressionParser();

    /**
     * 校验expression是否能通过rootObject的检测
     *
     * @param context    上下文
     * @param expression 表达式
     * @return 是否通过
     */
    @SuppressWarnings("ALL")
    public static Mono<Boolean> check(EvaluationContext context, String expression) {
        return PARSER.parseExpression(expression)
                .getValue(context, Mono.class);
    }
}
