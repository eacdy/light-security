package com.itmuch.lightsecurity.example.error;

import com.itmuch.lightsecurity.exception.LightSecurityException;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * WebFlux全局异常处理
 * 参考：https://www.baeldung.com/spring-webflux-errors
 *
 * @author itmuch.com
 */
public class GlobalErrorWebExceptionHandler extends
        AbstractErrorWebExceptionHandler {
    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, applicationContext);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(
            ErrorAttributes errorAttributes) {

        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = super.getError(request);
        Map<String, Object> errorPropertiesMap = super.getErrorAttributes(request, false);

        if (error instanceof LightSecurityException) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(BodyInserters.fromObject(errorPropertiesMap));
        }
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(errorPropertiesMap));
    }
}