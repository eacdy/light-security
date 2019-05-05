package com.itmuch.lightsecurity.jwt;

import com.itmuch.lightsecurity.constants.ConstantsSecurity;
import com.itmuch.lightsecurity.exception.LightSecurityException;
import com.itmuch.lightsecurity.reactive.ReactiveRequestContextHolder;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author itmuch.com
 */
@Slf4j
@AllArgsConstructor
public class ReactiveUserOperator {
    private final JwtOperator jwtOperator;
    private static final String LIGHT_SECURITY_REQ_ATTR_USER = "light-security-user";
    private static final int SEVEN = 7;


    public Mono<User> getUser() {
        return ReactiveRequestContextHolder.getRequest()
                .map(serverHttpRequest -> {
                            String token = getTokenFromRequest(serverHttpRequest);
                            return getUserFromToken(token);
                        }
                );
    }

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

    private String getTokenFromRequest(ServerHttpRequest request) {
        List<String> headers = request.getHeaders()
                .get(ConstantsSecurity.AUTHORIZATION_HEADER);
        if (CollectionUtils.isEmpty(headers)) {
            throw new LightSecurityException("没有找到名为Authorization的header");
        }
        String header = headers.get(0);
        if (!header.startsWith(ConstantsSecurity.BEARER)) {
            throw new LightSecurityException("token必须以'Bearer '开头");
        }
        if (header.length() <= SEVEN) {
            throw new LightSecurityException("token非法，长度 <= 7");
        }

        return header.substring(SEVEN);
    }
}
