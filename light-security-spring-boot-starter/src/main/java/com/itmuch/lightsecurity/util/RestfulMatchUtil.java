package com.itmuch.lightsecurity.util;

import com.itmuch.lightsecurity.enums.HttpMethod;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author itmuch.com
 */
@Slf4j
@UtilityClass
public class RestfulMatchUtil {
    private static final String MATCH_ALL = "/**";
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    /**
     * 判断request使用的请求方法与httpMethod一致且请求的路径与pattern一致
     * 参考了spring-security中org.springframework.security.web.util.matcher.AntPathRequestMatcher#matches(javax.servlet.http.HttpServletRequest)的写法。
     *
     * @param request    请求
     * @param httpMethod 配置的http请求方法
     * @param pattern    配置的路径pattern
     * @return 是否匹配
     */
    public static boolean match(HttpServletRequest request, HttpMethod httpMethod, String pattern) {
        boolean methodMatches = matchMethod(request, httpMethod);
        boolean pathMatches = matchPath(request, pattern);

        log.info("match begins. {} {}, httpMethod = {}, pattern = {}, methodMatch = {}, pathMatches = {}",
                request.getMethod(), getRequestPath(request),
                httpMethod, pattern, methodMatches, pathMatches
        );

        return methodMatches && pathMatches;
    }

    /**
     * 判断方法是否匹配
     *
     * @param request    请求
     * @param httpMethod 配置的httpMethod
     * @return 是否匹配
     */
    private static boolean matchMethod(HttpServletRequest request, HttpMethod httpMethod) {
        log.debug("method match begins. {} {}, httpMethod = {}",
                request.getMethod(), getRequestPath(request), httpMethod);
        if (httpMethod == HttpMethod.ANY) {
            return true;
        }
        return httpMethod != null && StringUtils.hasText(request.getMethod())
                && httpMethod == valueOf(request.getMethod());
    }

    /**
     * 判断路径是否匹配
     *
     * @param request 请求
     * @param pattern 配置的路径pattern
     * @return 是否匹配
     */
    private static boolean matchPath(HttpServletRequest request, String pattern) {
        String url = getRequestPath(request);
        log.debug("path match begins. {} {}, pattern = {}", request.getMethod(), url, pattern);
        // 如果pattern == /**，则直接认为匹配
        if (pattern.equals(MATCH_ALL)) {
            return true;
        }
        return MATCHER.match(pattern, url);
    }

    /**
     * 字符串转HttpMethod枚举
     *
     * @param method method
     * @return 枚举
     */
    private static HttpMethod valueOf(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    /**
     * 获取请求路径
     *
     * @param request 请求
     * @return 请求路径
     */
    private static String getRequestPath(HttpServletRequest request) {
        String url = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            url = StringUtils.hasLength(url) ? url + pathInfo : pathInfo;
        }
        return url;
    }
}