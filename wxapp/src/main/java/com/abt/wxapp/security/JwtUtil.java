package com.abt.wxapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT 工具类
 */
@Slf4j
@Component
public class JwtUtil {

    /** Token 默认有效天数（30 天） */
    @Value("${jwt.expire-days:30}")
    private long expireDays;

    /** 签名密钥，由各环境 profile 配置文件提供（dev 写死，prod 读环境变量） */
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT Token
     *
     * @param userDetails 登录用户信息
     * @return JWT 字符串
     */
    public String generateToken(WxUserDetails userDetails) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userDetails.getOpenid())
                .claim("userId", userDetails.getUserId())
                .claim("username", userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expireDays, ChronoUnit.DAYS)))
                .signWith(signingKey())
                .compact();
    }

    /**
     * 解析 Token，返回 Claims；Token 无效或过期时抛出 {@link JwtException}
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 校验 Token 是否有效
     */
    public boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT 校验失败: {}", e.getMessage());
            return false;
        }
    }

    /** 从 Token 中提取 openid */
    public String getOpenid(String token) {
        return parseToken(token).getSubject();
    }

    /** 从 Token 中提取 userId */
    public String getUserId(String token) {
        return parseToken(token).get("userId", String.class);
    }

    /** 从 Token 中提取 username */
    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /** 从 Token 中构建 WxUserDetails */
    public WxUserDetails getUserDetails(String token) {
        Claims claims = parseToken(token);
        return new WxUserDetails(
                claims.get("userId", String.class),
                claims.getSubject(),
                claims.get("username", String.class)
        );
    }
}
