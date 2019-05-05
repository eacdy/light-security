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
 * @author zhouli
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("WeakerAccess")
public class JwtOperator {
    public static final String USER_ID = "id";
    public static final String USERNAME = "username";
    public static final String ROLES = "roles";
    private final ReactiveLightSecurityProperties reactiveLightSecurityProperties;

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

    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationTime() {
        return new Date(System.currentTimeMillis() + reactiveLightSecurityProperties.getJwt().getExpirationInSecond() * 1000);
    }

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

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}