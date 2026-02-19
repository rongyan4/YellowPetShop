-- 订单确认和支付系统数据库脚本

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 用户地址表
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(100) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `province` varchar(50) NOT NULL COMMENT '省份',
  `city` varchar(50) NOT NULL COMMENT '城市',
  `district` varchar(50) NOT NULL COMMENT '区/县',
  `detail_address` varchar(500) NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址 1-是 0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户地址表' ROW_FORMAT = DYNAMIC;

-- 插入测试地址数据
INSERT INTO `user_address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `is_default`) 
VALUES 
(1, '张三', '13800138000', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城A座1001室', 1),
(1, '李四', '13900139000', '上海市', '上海市', '浦东新区', '世纪大道1号国金中心2号楼2001室', 0);

-- ----------------------------
-- 用户钱包表
-- ----------------------------
DROP TABLE IF EXISTS `user_wallet`;
CREATE TABLE `user_wallet` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
  `pay_password` varchar(255) NULL COMMENT '支付密码（加密存储）',
  `is_locked` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否锁定 1-锁定 0-正常',
  `lock_time` datetime NULL COMMENT '锁定时间',
  `error_count` int NOT NULL DEFAULT 0 COMMENT '密码错误次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `fk_wallet_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户钱包表' ROW_FORMAT = DYNAMIC;

-- 插入测试钱包数据（支付密码：123456，已加密）
INSERT INTO `user_wallet` (`user_id`, `balance`, `pay_password`) 
VALUES (1, 10000.00, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z58QhPQNLQX.JxJK7VYnOZOm');

-- ----------------------------
-- 支付记录表
-- ----------------------------
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `payment_method` varchar(20) NOT NULL COMMENT '支付方式: WALLET-钱包 WECHAT-微信 ALIPAY-支付宝',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '支付状态: PENDING-待支付 SUCCESS-成功 FAILED-失败',
  `transaction_no` varchar(100) NULL COMMENT '交易流水号',
  `pay_time` datetime NULL COMMENT '支付时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_payment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 修改订单表，增加支付方式字段
-- ----------------------------
ALTER TABLE `orders` 
ADD COLUMN `payment_method` varchar(20) NULL COMMENT '支付方式: WALLET-钱包 WECHAT-微信 ALIPAY-支付宝' AFTER `pay_amount`;

-- ----------------------------
-- 钱包流水表
-- ----------------------------
DROP TABLE IF EXISTS `wallet_transaction`;
CREATE TABLE `wallet_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_id` bigint NULL COMMENT '关联订单ID',
  `type` varchar(20) NOT NULL COMMENT '交易类型: RECHARGE-充值 PAYMENT-支付 REFUND-退款',
  `amount` decimal(10, 2) NOT NULL COMMENT '交易金额',
  `balance_before` decimal(10, 2) NOT NULL COMMENT '交易前余额',
  `balance_after` decimal(10, 2) NOT NULL COMMENT '交易后余额',
  `description` varchar(255) NULL COMMENT '交易描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  CONSTRAINT `fk_transaction_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '钱包流水表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
