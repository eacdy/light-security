package com.itmuch.lightsecurity.annotation;

import java.lang.annotation.*;

/**
 * 用于认证、鉴权的注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PreAuthorize {
    /**
     * 待验证的Spring-EL表达式
     * 参考：https://docs.spring.io/spring/docs/5.1.6.RELEASE/spring-framework-reference/core.html#expressions
     *
     * @return 表达式
     * @see com.itmuch.lightsecurity.el.ReactivePreAuthorizeExpressionRoot
     */
    String value();
}
