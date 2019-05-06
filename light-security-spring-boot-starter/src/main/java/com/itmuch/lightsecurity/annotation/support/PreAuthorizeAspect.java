package com.itmuch.lightsecurity.annotation.support;


import com.itmuch.lightsecurity.annotation.PreAuthorize;
import com.itmuch.lightsecurity.el.PreAuthorizeExpressionRoot;
import com.itmuch.lightsecurity.exception.LightSecurityException;
import com.itmuch.lightsecurity.util.SpringElCheckUtil;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * 处理PreAuthorize注解的切面
 *
 * @author itmuch.com
 */
@Aspect
@AllArgsConstructor
public class PreAuthorizeAspect {
    private final PreAuthorizeExpressionRoot preAuthorizeExpressionRoot;

    @Around("@annotation(com.itmuch.lightsecurity.annotation.PreAuthorize) ")
    public Object preAuth(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        if (method.isAnnotationPresent(PreAuthorize.class)) {
            PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

            String expression = preAuthorize.value();
            boolean check = SpringElCheckUtil.check(
                    new StandardEvaluationContext(preAuthorizeExpressionRoot),
                    expression
            );
            if (!check) {
                throw new LightSecurityException("Access Denied.");
            }
        }
        return point.proceed();
    }
}

