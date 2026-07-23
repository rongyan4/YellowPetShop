-- 修复支付密码问题的 SQL 脚本

-- 1. 检查 user_wallet 表结构
DESCRIBE user_wallet;

-- 2. 查看当前钱包数据
SELECT id, user_id, balance, 
       CASE 
           WHEN pay_password IS NULL THEN 'NULL'
           WHEN pay_password = '' THEN 'EMPTY'
           WHEN pay_password LIKE '$2a$%' THEN 'BCrypt格式正确'
           ELSE CONCAT('异常格式: ', LEFT(pay_password, 20))
       END AS password_status,
       is_locked, error_count
FROM user_wallet;

-- 3. 更新支付密码为正确的 BCrypt 格式
-- 注意：下面的哈希值是 "123456" 加密后的结果
-- 你需要运行 PayPasswordGenerator.java 来生成新的哈希值

-- 为用户 ID=1 设置支付密码 "123456"
UPDATE user_wallet 
SET pay_password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z58QhPQNLQX.JxJK7VYnOZOm',
    is_locked = 0,
    error_count = 0,
    lock_time = NULL
WHERE user_id = 1;

-- 4. 验证更新结果
SELECT id, user_id, 
       LEFT(pay_password, 30) AS password_preview,
       LENGTH(pay_password) AS password_length,
       is_locked, error_count
FROM user_wallet
WHERE user_id = 1;

-- 5. 如果表结构有问题，重建表（谨慎使用！）
-- DROP TABLE IF EXISTS user_wallet;
-- CREATE TABLE user_wallet (
--   id bigint NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
--   user_id bigint NOT NULL COMMENT '用户ID',
--   balance decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
--   pay_password varchar(255) NULL COMMENT '支付密码（BCrypt加密）',
--   is_locked tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否锁定',
--   lock_time datetime NULL COMMENT '锁定时间',
--   error_count int NOT NULL DEFAULT 0 COMMENT '密码错误次数',
--   create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   PRIMARY KEY (id),
--   UNIQUE KEY uk_user_id (user_id),
--   CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户钱包表';
