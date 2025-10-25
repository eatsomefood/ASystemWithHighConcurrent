package com.star.highconcurrent.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类（基于 JJWT 0.11.5+ 版本 API）
 */
@Component
public class JWTUtil {

    /**
     * 签名密钥（建议在配置文件中配置，长度至少 256 位以满足 HS256 要求）
     */
    @Value("${jwt.secret:5f4dcc3b5aa765d61d8327deb882cf99a61c41e383d3a7e92b64819fa0e264}")
    private String secretKey;

    /**
     * 令牌过期时间（毫秒，默认 1 小时）
     */
    @Value("${jwt.ttl:3600000}")
    private Long ttl;

    private static final String SUBJECT = "ALL";

    /**
     * 生成签名密钥对象（基于 HS256 算法）
     */
    private SecretKey getSigningKey() {
        // 确保密钥字节长度满足算法要求（HS256 需要至少 32 字节 = 256 位）
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 创建 JWT 令牌
     * @param claims 自定义载荷信息
     * @return 加密后的 JWT 字符串
     */
    public String createJwt(Map<String, Object> claims) {
        // 计算过期时间
        long expMills = System.currentTimeMillis() + ttl;
        Date expirationDate = new Date(expMills);

        // 使用新版本 Builder API 构建令牌
        JwtBuilder jwtBuilder = Jwts.builder()
                .setIssuer("star")                  // 签发者
                .setIssuedAt(new Date())            // 签发时间
                .setSubject(SUBJECT)                // 主题
                .addClaims(claims)                  // 自定义载荷（替代旧版 addClaims）
                .setExpiration(expirationDate)      // 过期时间
                .signWith(getSigningKey());      // 签名（新版本无需显式指定算法，由密钥类型决定）

        return jwtBuilder.compact();
    }

    /**
     * 解析 JWT 令牌
     * @param jwt JWT 字符串
     * @return 解析后的载荷信息
     * @throws io.jsonwebtoken.JwtException 当令牌无效、过期、签名错误等情况时抛出
     */
    public Claims parseJWT(String jwt) {
        // 使用新版本 ParserBuilder API 解析令牌
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // 设置签名密钥
                .build()                         // 构建解析器
                .parseClaimsJws(jwt)             // 解析 JWT
                .getBody();                      // 获取载荷
    }

}