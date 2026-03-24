package com.yellow.petshop.model.token;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * Refresh Token 持久化实体
 * token_hash 存 SHA-256 哈希，不存明文 RT
 */
@TableName("refresh_token")
public class RefreshToken {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** RT 的 SHA-256 哈希（不存明文） */
    private String tokenHash;

    private Long userId;

    /** customer / merchant */
    private String userType;

    private String username;

    private LocalDateTime expireTime;

    private LocalDateTime createTime;

    /** 0-有效  1-已吊销 */
    private Integer revoked;

    // ===== getter / setter =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public Integer getRevoked() { return revoked; }
    public void setRevoked(Integer revoked) { this.revoked = revoked; }
}
