package com.yellow.petshop.util;

import com.yellow.petshop.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {
    private long expirationTime = 1000 * 60 * 60 * 24 * 7;
    private String secret = "2F9s7k8d6j5g4h3f2d1s0a9s8d7f6g5h4j3k2l1m0n9b8v7c6x5z4a8s7d6f5g4h3j2=";

    //生成token
    public String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                //Header
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                //Payload
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setId(UUID.randomUUID().toString())
                //Signature
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 从Token中提取用户ID
    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    // 验证Token是否有效（未过期 + 签名正确）
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false; // Token无效或过期
        }
    }

    // 提取Token负载
    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 判断Token是否过期
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
