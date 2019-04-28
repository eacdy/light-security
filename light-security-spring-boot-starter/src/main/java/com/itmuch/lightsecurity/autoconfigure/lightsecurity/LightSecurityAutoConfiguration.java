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

/**
 * 自动配置类
 *
 * @author itmuch.com
 */
@AllArgsConstructor
@Data
@Configuration
@Import(LightSecurityConfiguration.class)
public class LightSecurityAutoConfiguration implements WebMvcConfigurer {
    private List<Spec> specList;
    private PreAuthorizeExpressionRoot preAuthorizeExpressionRoot;

    /**
     * 添加拦截器
     * 注：这里之所以不将配置中anon()的路径添加到excludePathPatterns，是因为如下场景下发生异常难以定位：
     * GET /login anon()
     * ANY /** hasLogin()
     * 然后访问/login报异常了，Spring Boot会重定向到/error，而此时并未配置/error的访问规则
     * 于是命中到/**，于是报没有添加header的异常。
     * 而此时一行相关日志都没有，定位问题不方便；
     * <p>
     * 而不过滤，则会打印类似如下的日志：
     * c.i.lightsecurity.util.RestfulMatchUtil  : match begins. GET /error, httpMethod = ANY, pattern = /login, methodMatch = true, pathMatches = false
     * 问题定位会方便很多。
     *
     * @param registry registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                new AuthInterceptor(specList, preAuthorizeExpressionRoot)
        );
    }
}