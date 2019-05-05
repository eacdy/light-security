package com.itmuch.lightsecurity.autoconfigure.lightsecurity;

import com.itmuch.lightsecurity.el.ReactivePreAuthorizeExpressionRoot;
import com.itmuch.lightsecurity.reactive.AuthWebFilter;
import com.itmuch.lightsecurity.reactive.ReactorContextWebFilter;
import com.itmuch.lightsecurity.spec.Spec;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @author itmuch.com
 */
@Data
@Configuration
@Import(ReactiveLightSecurityConfiguration.class)
public class ReactiveLightSecurityAutoConfiguration {
    @Bean
    public AuthWebFilter authWebFilter(List<Spec> specList, ReactivePreAuthorizeExpressionRoot reactivePreAuthorizeExpressionRoot) {
        return new AuthWebFilter(specList, reactivePreAuthorizeExpressionRoot);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ReactorContextWebFilter reactorContextWebFilter() {
        return new ReactorContextWebFilter();
    }
}
