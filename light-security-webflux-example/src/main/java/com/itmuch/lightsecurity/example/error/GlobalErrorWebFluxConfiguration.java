package com.itmuch.lightsecurity.example.error;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.List;

/**
 * WebFlux全局异常处理配置类
 * 参考：org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration#errorWebExceptionHandler(org.springframework.boot.web.reactive.error.ErrorAttributes)
 * 参考：https://www.baeldung.com/spring-webflux-errors
 *
 * @author itmuch.com
 */
@Configuration
public class GlobalErrorWebFluxConfiguration {
    private final ApplicationContext applicationContext;

    private final ResourceProperties resourceProperties;

    private final List<ViewResolver> viewResolvers;

    private final ServerCodecConfigurer serverCodecConfigurer;

    public GlobalErrorWebFluxConfiguration(
            ApplicationContext applicationContext,
            ResourceProperties resourceProperties,
            List<ViewResolver> viewResolvers,
            ServerCodecConfigurer serverCodecConfigurer
    ) {
        this.applicationContext = applicationContext;
        this.resourceProperties = resourceProperties;
        this.viewResolvers = viewResolvers;
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(-2)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
        GlobalErrorWebExceptionHandler exceptionHandler = new GlobalErrorWebExceptionHandler(
                errorAttributes,
                this.resourceProperties,
                this.applicationContext
        );

        exceptionHandler.setViewResolvers(this.viewResolvers);
        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }
}
