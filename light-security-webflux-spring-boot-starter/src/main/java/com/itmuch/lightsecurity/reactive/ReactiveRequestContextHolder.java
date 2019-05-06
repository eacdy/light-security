package com.itmuch.lightsecurity.reactive;

import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

/**
 * 实现静态方法拿request
 * 参考：https://juejin.im/post/5ca5c18bf265da30ce02928f
 *
 * @author L.cm
 * @author itmuch.com
 */
public class ReactiveRequestContextHolder {
    static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.subscriberContext()
                .map(ctx -> ctx.get(CONTEXT_KEY));
    }

}