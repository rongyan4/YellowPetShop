package com.yellow.petshop.util;

import com.yellow.petshop.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {
    private long expirationTime = 1000 * 60 * 60 * 24 * 7;
    @Value("${jwt.secret}")
    private String secret = "2F9s7k8d6j5g4h3f2d1s0a9s8d7f6g5h4j3k2l1m0n9b8v7c6x5z4a8s7d6f5g4h3j2=";

    //生成token（使用User对象 - 客户端）
    public String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                //Header
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                //Payload
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("userType", "customer")  // 标识为客户端用户
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setId(UUID.randomUUID().toString())
                //Signature
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    //生成token（使用ID和用户名 - 商家端）
    public static String generateToken(Long userId, String username) {
        JwtUtil jwtUtil = new JwtUtil();
        SecretKey key = Keys.hmacShaKeyFor(jwtUtil.secret.getBytes());
        return Jwts.builder()
                //Header
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                //Payload
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("userType", "merchant")  // 标识为商家端用户
                .setExpiration(new Date(System.currentTimeMillis() + jwtUtil.expirationTime))
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

    //提取用户名
    public String extractusername(String token) {
        Claims claims = extractClaims(token);
        return claims.get("username", String.class);
    }
    
    //提取用户类型
    public String extractUserType(String token) {
        Claims claims = extractClaims(token);
        return claims.get("userType", String.class);
    }
    
    // 静态方法：从Token中获取用户类型
    public static String getUserTypeFromToken(String token) {
        try {
            JwtUtil jwtUtil = new JwtUtil();
            return jwtUtil.extractUserType(token);
        } catch (Exception e) {
            return null;
        }
    }
    
    // 验证Token是否为商家端token
    public static boolean isMerchantToken(String token) {
        String userType = getUserTypeFromToken(token);
        return "merchant".equals(userType);
    }
    
    // 验证Token是否为客户端token
    public static boolean isCustomerToken(String token) {
        String userType = getUserTypeFromToken(token);
        return "customer".equals(userType);
    }
    
    // 静态方法：从Token中获取用户ID（用于Controller）
    public static Long getUserIdFromToken(String token) {
        try {
            JwtUtil jwtUtil = new JwtUtil();
            return jwtUtil.extractUserId(token);
        } catch (Exception e) {
            return null;
        }
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
