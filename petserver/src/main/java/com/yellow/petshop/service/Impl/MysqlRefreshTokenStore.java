package com.yellow.petshop.service.Impl;

import com.yellow.petshop.mapper.RefreshTokenMapper;
import com.yellow.petshop.model.token.RefreshToken;
import com.yellow.petshop.service.RefreshTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

/**
 * MySQL 实现的 Refresh Token 存储
 * <p>
 * 若要切换为 Redis，新建 RedisRefreshTokenStore implements RefreshTokenStore，
 * 删除本类的 @Service 注解（或使用 @Primary / @Qualifier 切换），业务层无需改动。
 * </p>
 */
@Service
public class MysqlRefreshTokenStore implements RefreshTokenStore {

    @Autowired
    private RefreshTokenMapper refreshTokenMapper;

    @Override
    public void save(String rawToken, Long userId, String userType, String username, long ttlMs) {
        // 优先直接覆盖该用户 RT（满足“直接更新库中 RT”）；若无历史记录则插入
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusNanos(ttlMs * 1_000_000L);
        int affected = refreshTokenMapper.updateByUser(
                hash(rawToken), userId, userType, username, expireTime, now
        );
        if (affected > 0) {
            return;
        }

        // 无记录时插入
        RefreshToken rt = new RefreshToken();
        rt.setTokenHash(hash(rawToken));
        rt.setUserId(userId);
        rt.setUserType(userType);
        rt.setUsername(username);
        rt.setExpireTime(expireTime);
        rt.setCreateTime(now);
        rt.setRevoked(0);
        refreshTokenMapper.insert(rt);
    }

    @Override
    public RefreshToken validate(String rawToken) {
        RefreshToken rt = refreshTokenMapper.findByTokenHash(hash(rawToken));
        if (rt == null) return null;
        if (rt.getRevoked() == 1) return null;
        if (rt.getExpireTime().isBefore(LocalDateTime.now())) return null;
        return rt;
    }

    @Override
    public void revoke(String rawToken) {
        refreshTokenMapper.revokeByTokenHash(hash(rawToken));
    }

    /**
     * 计算 SHA-256 哈希（Hex 字符串，64位）
     */
    private static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("RT哈希计算失败", e);
        }
    }
}
