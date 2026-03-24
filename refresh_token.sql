-- Refresh Token 持久化表
-- 用于有状态双Token方案，支持服务端主动吊销RT
-- 后续可替换为Redis实现，只需更换 RefreshTokenStore 接口的实现类

CREATE TABLE IF NOT EXISTS `refresh_token` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `token_hash`  VARCHAR(64)  NOT NULL COMMENT 'RT的SHA-256哈希值（不存明文）',
  `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
  `user_type`   VARCHAR(20)  NOT NULL COMMENT '用户类型：customer / merchant',
  `username`    VARCHAR(100) NOT NULL COMMENT '用户名（冗余，便于刷新时生成AT）',
  `expire_time` DATETIME     NOT NULL COMMENT 'RT过期时间',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `revoked`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否已吊销：0-有效 1-已吊销',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_token_hash` (`token_hash`),
  INDEX `idx_user` (`user_id`, `user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Refresh Token持久化表';
