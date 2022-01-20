package com.itmuch.lightsecurity.util;

import com.itmuch.lightsecurity.exception.LightSecurityException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class WebUtil {
    public static Optional<Cookie> filterCookieByName(Cookie[] cookies, String cookieName) {
        if(cookies == null || cookies.length == 0) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(c-> Objects.equals(c.getName(), cookieName))
                .findFirst();
    }


    /**
     * 获取request
     *
     * @return request
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if ((requestAttributes == null)) {
            throw new LightSecurityException("requestAttributes为null");
        }
        return ((ServletRequestAttributes) requestAttributes)
                .getRequest();
    }
}
