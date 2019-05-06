package com.itmuch.lightsecurity.reactive;

import com.itmuch.lightsecurity.el.ReactivePreAuthorizeExpressionRoot;
import com.itmuch.lightsecurity.exception.LightSecurityException;
import com.itmuch.lightsecurity.spec.Spec;
import com.itmuch.lightsecurity.util.ReactiveRestfulMatchUtil;
import com.itmuch.lightsecurity.util.ReactiveSpringElCheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 授权控制过滤器
 *
 * @author itmuch.com
 */
@RequiredArgsConstructor
public class AuthWebFilter implements WebFilter {
    private final List<Spec> specList;
    private final ReactivePreAuthorizeExpressionRoot reactivePreAuthorizeExpressionRoot;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        Mono<Boolean> mono = specList.stream()
                .filter(spec -> ReactiveRestfulMatchUtil.match(request, spec.getHttpMethod(), spec.getPath()))
                .findFirst()
                .map(spec -> {
                    String expression = spec.getExpression();
                    return ReactiveSpringElCheckUtil.check(
                            new StandardEvaluationContext(reactivePreAuthorizeExpressionRoot),
                            expression
                    );

                })
                .orElse(Mono.just(true));

        return mono.filter(t -> t)
                .switchIfEmpty(Mono.error(new LightSecurityException("Access Denied")))
                .flatMap(t -> chain.filter(exchange));
    }
}
