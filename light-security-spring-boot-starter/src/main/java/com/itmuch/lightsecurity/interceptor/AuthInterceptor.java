package com.itmuch.lightsecurity.interceptor;

import com.itmuch.lightsecurity.el.PreAuthorizeExpressionRoot;
import com.itmuch.lightsecurity.exception.LightSecurityException;
import com.itmuch.lightsecurity.spec.Spec;
import com.itmuch.lightsecurity.util.RestfulMatchUtil;
import com.itmuch.lightsecurity.util.SpringElCheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 授权控制拦截器
 *
 * @author itmuch.com
 */
@RequiredArgsConstructor
public class AuthInterceptor extends HandlerInterceptorAdapter {
    private final List<Spec> specList;
    private final PreAuthorizeExpressionRoot preAuthorizeExpressionRoot;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 当前请求的路径和定义的规则能够匹配
        Boolean checkResult = specList.stream()
                .filter(spec -> RestfulMatchUtil.match(request, spec.getHttpMethod(), spec.getPath()))
                .findFirst()
                .map(spec -> {
                    String expression = spec.getExpression();
                    return SpringElCheckUtil.check(
                            new StandardEvaluationContext(preAuthorizeExpressionRoot),
                            expression
                    );

                })
                .orElse(true);
        if (!checkResult) {
            throw new LightSecurityException("Access Denied.");
        }
        return true;
    }
}

