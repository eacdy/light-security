package com.itmuch.lightsecurity.jwt;

import com.itmuch.lightsecurity.autoconfigure.lightsecurity.LightSecurityProperties;
import com.itmuch.lightsecurity.exception.LightSecurityException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouli
 */
@Slf4j
@RequiredArgsConstructor
public class JwtOperator {
    public static final String USER_ID = "id";
    public static final String USERNAME = "username";
    public static final String ROLES = "roles";
    private final LightSecurityProperties lightSecurityProperties;

    public Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            return Jwts.parser()
                    .setSigningKey(lightSecurityProperties.getJwt().getSecret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | SignatureException | MalformedJwtException | IllegalArgumentException e) {
            log.error("token解析错误", e);
            throw new LightSecurityException("Token invalided.", e);
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

//    public String generateToken(String username, Set<String> roles) {
//        Map<String, Object> claims = new HashMap<>(1);
//        claims.put("roles", roles);
//        return this.doGenerateToken(claims);
//    }

    public Date getExpirationTime() {
        return new Date(System.currentTimeMillis() + lightSecurityProperties.getJwt().getExpirationInSecond() * 1000);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, user.getId());
        claims.put(USERNAME, user.getUsername());
        claims.put(ROLES, user.getRoles());
        Date createdTime = new Date();
        Date expirationTime = this.getExpirationTime();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(createdTime)
                .setExpiration(expirationTime)
                .signWith(lightSecurityProperties.getJwt().getAlgorithm(), lightSecurityProperties.getJwt().getSecret())
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

}