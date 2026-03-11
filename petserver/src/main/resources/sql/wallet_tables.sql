-- 钱包交易记录表
CREATE TABLE IF NOT EXISTS `wallet_transaction` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '交易ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) NOT NULL COMMENT '交易类型：RECHARGE-充值, WITHDRAW-提现, DEDUCT-扣款, ADD-增加',
  `amount` DECIMAL(10, 2) NOT NULL COMMENT '交易金额',
  `balance_before` DECIMAL(10, 2) NOT NULL COMMENT '交易前余额',
  `balance_after` DECIMAL(10, 2) NOT NULL COMMENT '交易后余额',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包交易记录表';

-- 如果user_wallet表不存在，创建它
CREATE TABLE IF NOT EXISTS `user_wallet` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `balance` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
  `pay_password` VARCHAR(255) DEFAULT NULL COMMENT '支付密码（加密）',
  `is_locked` TINYINT(1) DEFAULT 0 COMMENT '是否锁定：0-否，1-是',
  `lock_time` DATETIME DEFAULT NULL COMMENT '锁定时间',
  `error_count` INT DEFAULT 0 COMMENT '密码错误次数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包表';
