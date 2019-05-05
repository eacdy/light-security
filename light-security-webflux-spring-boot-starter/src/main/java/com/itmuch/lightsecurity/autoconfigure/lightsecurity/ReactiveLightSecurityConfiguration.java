package com.itmuch.lightsecurity.autoconfigure.lightsecurity;

import com.itmuch.lightsecurity.annotation.support.ReactivePreAuthorizeAspect;
import com.itmuch.lightsecurity.el.ReactivePreAuthorizeExpressionRoot;
import com.itmuch.lightsecurity.jwt.JwtOperator;
import com.itmuch.lightsecurity.jwt.ReactiveUserOperator;
import com.itmuch.lightsecurity.spec.Spec;
import com.itmuch.lightsecurity.spec.SpecRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 配置类
 *
 * @author itmuch.com
 */
@Configuration
@EnableConfigurationProperties(ReactiveLightSecurityProperties.class)
@AutoConfigureBefore(ReactiveLightSecurityAutoConfiguration.class)
public class ReactiveLightSecurityConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public JwtOperator jwtOperator(ReactiveLightSecurityProperties reactiveLightSecurityProperties) {
        return new JwtOperator(reactiveLightSecurityProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReactiveUserOperator userOperator(JwtOperator jwtOperator) {
        return new ReactiveUserOperator(jwtOperator);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReactivePreAuthorizeExpressionRoot preAuthorizeExpressionRoot(ReactiveUserOperator userOperator) {
        return new ReactivePreAuthorizeExpressionRoot(userOperator);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReactivePreAuthorizeAspect preAuthorizeAspect(ReactivePreAuthorizeExpressionRoot reactivePreAuthorizeExpressionRoot) {
        return new ReactivePreAuthorizeAspect(reactivePreAuthorizeExpressionRoot);
    }

    @Bean
    @ConditionalOnBean(SpecRegistry.class)
    public List<Spec> specListFromSpecRegistry(SpecRegistry specRegistry) {
        List<Spec> specList = specRegistry.getSpecList();
        if (CollectionUtils.isEmpty(specList)) {
            throw new IllegalArgumentException("specList cannot be empty.");
        }
        return specList;
    }

    @Bean
    @ConditionalOnMissingBean(SpecRegistry.class)
    public List<Spec> specListFromProperties(ReactiveLightSecurityProperties reactiveLightSecurityProperties) {
        return reactiveLightSecurityProperties.getSpecList();
    }
}
