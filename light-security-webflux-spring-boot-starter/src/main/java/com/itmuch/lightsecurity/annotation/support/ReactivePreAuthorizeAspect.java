package com.itmuch.lightsecurity.annotation.support;


import com.itmuch.lightsecurity.annotation.PreAuthorize;
import com.itmuch.lightsecurity.el.ReactivePreAuthorizeExpressionRoot;
import com.itmuch.lightsecurity.exception.LightSecurityException;
import com.itmuch.lightsecurity.util.ReactiveSpringElCheckUtil;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.reactivestreams.Publisher;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

/**
 * 处理PreAuthorize的切面
 *
 * @author itmuch.com
 */
@Aspect
@AllArgsConstructor
public class ReactivePreAuthorizeAspect {
    private final ReactivePreAuthorizeExpressionRoot reactivePreAuthorizeExpressionRoot;

    /**
     * 参考了org.springframework.security.access.prepost.PrePostAdviceReactiveMethodInterceptor#invoke的写法
     *
     * @param point 切点
     * @return obj
     */
    @Around("@annotation(com.itmuch.lightsecurity.annotation.PreAuthorize) ")
    public Object preAuth(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        if (method.isAnnotationPresent(PreAuthorize.class)) {
            PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

            String expression = preAuthorize.value();
            Mono<Boolean> mono = ReactiveSpringElCheckUtil.check(
                    new StandardEvaluationContext(reactivePreAuthorizeExpressionRoot),
                    expression)
                    .filter(t -> t)
                    .switchIfEmpty(Mono.defer(() -> Mono.error(new LightSecurityException("Access Denied."))));

            Class<?> returnType = method.getReturnType();

            if (Mono.class.isAssignableFrom(returnType)) {
                return mono
                        .flatMap(
                                auth -> this.proceed(point)
                        );
            }

            if (Flux.class.isAssignableFrom(returnType)) {
                return mono
                        .flatMapMany(
                                auth -> this.<Flux<?>>proceed(point)
                        );
            }

            return mono
                    .flatMapMany(auth -> Flux.from(
                            this.proceed(point))
                    );

        }
        return this.proceed(point);
    }

    @SuppressWarnings("ALL")
    private <T extends Publisher<?>> T proceed(ProceedingJoinPoint point) {
        try {
            return (T) point.proceed();
        } catch (Throwable throwable) {
            throw Exceptions.propagate(throwable);
        }
    }
}

