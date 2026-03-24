package com.yellow.petshop.service;

import com.yellow.petshop.model.token.RefreshToken;

/**
 * Refresh Token 存储抽象接口
 * <p>
 * 当前使用 MySQL 实现（{@link com.yellow.petshop.service.Impl.MysqlRefreshTokenStore}）。
 * 若需切换为 Redis，只需新建 RedisRefreshTokenStore 实现本接口，
 * 并将 Spring Bean 注入替换即可，业务代码无需任何修改。
 * </p>
 */
public interface RefreshTokenStore {

    /**
     * 保存新的 RT（同时吊销该用户的所有旧 RT）
     *
     * @param rawToken  RT 明文字符串（只用于计算哈希，不持久化）
     * @param userId    用户ID
     * @param userType  用户类型：customer / merchant
     * @param username  用户名
     * @param ttlMs     RT 有效期（毫秒）
     */
    void save(String rawToken, Long userId, String userType, String username, long ttlMs);

    /**
     * 验证 RT 是否有效（存在 + 未吊销 + 未过期）
     *
     * @param rawToken RT 明文
     * @return 对应的存储记录，验证失败返回 null
     */
    RefreshToken validate(String rawToken);

    /**
     * 吊销 RT（登出时调用）
     *
     * @param rawToken RT 明文
     */
    void revoke(String rawToken);
}
