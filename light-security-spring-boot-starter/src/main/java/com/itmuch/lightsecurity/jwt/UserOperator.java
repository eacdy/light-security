package com.itmuch.lightsecurity.jwt;

import com.itmuch.lightsecurity.constants.ConstantsSecurity;
import com.itmuch.lightsecurity.util.WebUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * @author itmuch.com
 */
@Slf4j
@AllArgsConstructor
public class UserOperator {
    private static final int SEVEN = 7;

    private final JwtOperator jwtOperator;

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    public LoginUser getUser() {
        try {
            HttpServletRequest request = WebUtil.getRequest();
            Object userInReq = request.getAttribute(ConstantsSecurity.LIGHT_SECURITY_REQ_ATTR_USER);
            if (userInReq != null) {
                return (LoginUser) userInReq;
            }

            String token = this.getTokenFromRequest(request);
            if (ObjectUtils.isEmpty(token)) {
                return null;
            }

            Boolean isValid = jwtOperator.validateToken(token);
            if (!isValid) {
                log.warn("token is not valided. token = {}", token);
                return null;
            }

            LoginUser loginUser = this.getUserFromToken(token);
            request.setAttribute(ConstantsSecurity.LIGHT_SECURITY_REQ_ATTR_USER, loginUser);
            return loginUser;
        } catch (Exception e) {
            log.info("发生异常", e);
            return null;
        }
    }

    /**
     * 解析token，获得用户信息
     *
     * @param token token
     * @return 用户信息
     */
    @SuppressWarnings("unchecked")
    private LoginUser getUserFromToken(String token) {
        // 从token中获取user
        Claims claims = jwtOperator.getClaimsFromToken(token);
        Object roles = claims.get(JwtOperator.ROLES);
        Object userId = claims.get(JwtOperator.USER_ID);
        Object username = claims.get(JwtOperator.USERNAME);

        return LoginUser.builder()
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
        // 从header中获取
        String token = request.getHeader(ConstantsSecurity.AUTHORIZATION);
        // 如果header中获取不到，则从param中获取
        if (ObjectUtils.isEmpty(token)) {
            token = request.getParameter(ConstantsSecurity.AUTHORIZATION);
        }
        // 如果param中也取不到，则从cookie中获取
        if (ObjectUtils.isEmpty(token)) {
            Cookie[] cookies = request.getCookies();
            Optional<Cookie> cookieOptional = WebUtil.filterCookieByName(cookies, ConstantsSecurity.AUTHORIZATION);
            token = cookieOptional.map(Cookie::getValue)
                    .orElse(null);
        }
        // 如果依然找不到，返回空
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        // 如果token不以Bearer开头，则直接使用，无需去掉Bearer 前缀
        if (!token.startsWith(ConstantsSecurity.BEARER)) {
            log.warn("建议Token以'Bearer '开头，形如`Bearer 你的token`");
            return token;
        }
        return token.substring(SEVEN);
    }
}
