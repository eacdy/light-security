package com.itmuch.lightsecurity.autoconfigure.lightsecurity;

import com.itmuch.lightsecurity.el.PreAuthorizeExpressionRoot;
import com.itmuch.lightsecurity.interceptor.AuthInterceptor;
import com.itmuch.lightsecurity.spec.Spec;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 自动配置类
 *
 * @author itmuch.com
 */
@AllArgsConstructor
@Data
@Configuration
@Import(LightSecurityConfiguration.class)
@EnableConfigurationProperties(LightSecurityProperties.class)
class LightSecurityAutoConfiguration implements WebMvcConfigurer {
    private List<Spec> specList;
    private PreAuthorizeExpressionRoot preAuthorizeExpressionRoot;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 过滤掉配置匿名访问的路径
        List<Spec> specs = specList
                .stream()
                .filter(spec -> !Objects.equals("anon()", spec.getExpression()))
                .collect(Collectors.toList());

        List<String> anonPaths = specList.stream()
                .filter(spec -> Objects.equals("anon()", spec.getExpression()))
                .map(Spec::getPath)
                .collect(Collectors.toList());


        List<String> pathNeedToAuth = specs.stream()
                .map(Spec::getPath)
                .collect(Collectors.toList());

        registry.addInterceptor(
                new AuthInterceptor(specs, preAuthorizeExpressionRoot)
        ).addPathPatterns(pathNeedToAuth)
                .excludePathPatterns(anonPaths);
    }
}