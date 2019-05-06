package com.itmuch.lightsecurity.jwt;

import com.itmuch.lightsecurity.autoconfigure.lightsecurity.ReactiveLightSecurityProperties;
import com.itmuch.lightsecurity.exception.LightSecurityException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author itmuch.com
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("WeakerAccess")
public class JwtOperator {
    public static final String USER_ID = "id";
    public static final String USERNAME = "username";
    public static final String ROLES = "roles";
    private final ReactiveLightSecurityProperties reactiveLightSecurityProperties;

    /**
     * 从token中获取claim
     *
     * @param token token
     * @return claim
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(this.reactiveLightSecurityProperties.getJwt().getSecret().getBytes())
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            log.error("token解析错误", e);
            throw new LightSecurityException(HttpStatus.UNAUTHORIZED, "Token invalided.", e);
        }
    }

    /**
     * 获取token的过期时间
     *
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    /**
     * 判断token是否过期
     *
     * @param token token
     * @return 已过期返回true，未过期返回false
     */
    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 计算token的过期时间
     *
     * @return 过期时间
     */
    private Date getExpirationTime() {
        return new Date(System.currentTimeMillis() + reactiveLightSecurityProperties.getJwt().getExpirationInSecond() * 1000);
    }

    /**
     * 为指定用户生成token
     *
     * @param user 用户信息
     * @return token
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>(3);
        claims.put(USER_ID, user.getId());
        claims.put(USERNAME, user.getUsername());
        claims.put(ROLES, user.getRoles());
        Date createdTime = new Date();
        Date expirationTime = this.getExpirationTime();

        byte[] keyBytes = this.reactiveLightSecurityProperties.getJwt().getSecret().getBytes();
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(createdTime)
                .setExpiration(expirationTime)
                .signWith(key)
                .compact();
    }

    /**
     * 判断token是否非法
     *
     * @param token token
     * @return 未过期返回true，否则返回false
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}