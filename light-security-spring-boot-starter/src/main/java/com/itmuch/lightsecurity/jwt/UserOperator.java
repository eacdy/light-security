package com.itmuch.lightsecurity.jwt;

import com.itmuch.lightsecurity.constants.ConstantsSecurity;
import com.itmuch.lightsecurity.exception.LightSecurityException;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author itmuch.com
 */
@Slf4j
@AllArgsConstructor
public class UserOperator {
    private static final String LIGHT_SECURITY_REQ_ATTR_USER = "light-security-user";
    private static final int SEVEN = 7;

    private final JwtOperator jwtOperator;

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    public User getUser() {
        try {
            HttpServletRequest request = getRequest();
            String token = getTokenFromRequest(request);
            Boolean isValid = jwtOperator.validateToken(token);
            if (!isValid) {
                return null;
            }

            Object userInReq = request.getAttribute(LIGHT_SECURITY_REQ_ATTR_USER);
            if (userInReq != null) {
                return (User) userInReq;
            }
            User user = getUserFromToken(token);
            request.setAttribute(LIGHT_SECURITY_REQ_ATTR_USER, user);
            return user;
        } catch (Exception e) {
            log.info("发生异常", e);
            throw new LightSecurityException(e);
        }
    }

    /**
     * 解析token，获得用户信息
     *
     * @param token token
     * @return 用户信息
     */
    @SuppressWarnings("unchecked")
    private User getUserFromToken(String token) {
        // 从token中获取user
        Claims claims = jwtOperator.getClaimsFromToken(token);
        Object roles = claims.get(JwtOperator.ROLES);
        Object userId = claims.get(JwtOperator.USER_ID);
        Object username = claims.get(JwtOperator.USERNAME);

        return User.builder()
                .id((Integer) userId)
                .username((String) username)
                .roles((List<String>) roles)
                .build();
    }

    /**
     * 从request中获取token
     *
     * @param request 请求
     * @return token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(ConstantsSecurity.AUTHORIZATION_HEADER);
        if (StringUtils.isEmpty(header)) {
            throw new LightSecurityException("没有找到名为Authorization的header");
        }
        if (!header.startsWith(ConstantsSecurity.BEARER)) {
            throw new LightSecurityException("token必须以'Bearer '开头");
        }
        if (header.length() <= SEVEN) {
            throw new LightSecurityException("token非法，长度 <= 7");
        }
        return header.substring(SEVEN);
    }

    /**
     * 获取request
     *
     * @return request
     */
    private static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if ((requestAttributes == null)) {
            throw new LightSecurityException("requestAttributes为null");
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
}
