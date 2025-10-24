package com.star.highconcurrent.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JWTUtil {

    @Value("${com.star.jwt.secret-key:star}")
    private static String secretKey;
    // 设置签名
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    // 过期时间
    @Value("${com.star.jwt.exp:360000}")
    private static Long ttl;
    private static final String SUBJECT = "ALL";
    // 加密JWT令牌
    public static String createJwt(Map<String, Object> claims) {
        // 过期时间
        long expMills = System.currentTimeMillis() + ttl;
        Date date =new Date(expMills);
        // 创造JWT令牌
        JwtBuilder jwtBuilder = Jwts.builder()
                .setIssuer("star")
                .setIssuedAt(new Date())
                .setSubject(SUBJECT)
                .addClaims(claims)
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                .setExpiration(date);
        return jwtBuilder.compact();
    }

    // 解密
    public static Map<String,Object> parseJWT(String jwt){
        Claims body = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
        return body;
    }

}
