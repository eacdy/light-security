package com.itmuch.lightsecurity.reactive;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 实现静态方法拿request
 * 参考：https://juejin.im/post/5ca5c18bf265da30ce02928f
 *
 * @author L.cm
 * @author itmuch.com
 */
public class ReactorContextWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        return chain.filter(exchange)
                .subscriberContext(ctx -> ctx.put(ReactiveRequestContextHolder.CONTEXT_KEY, request));
    }
}
