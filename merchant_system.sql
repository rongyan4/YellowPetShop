-- ====================================
-- 商家端管理系统数据库表结构
-- 创建时间: 2026-02-19
-- ====================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 商家表
-- ----------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商家ID',
  `username` varchar(50) NOT NULL COMMENT '商家账号',
  `password` varchar(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `merchant_name` varchar(100) NOT NULL COMMENT '商家名称',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- 商家登录日志表
-- ----------------------------
DROP TABLE IF EXISTS `merchant_login_log`;
CREATE TABLE `merchant_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '浏览器信息',
  `login_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '登录状态 1-成功 0-失败',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_merchant_id` (`merchant_id`) USING BTREE,
  KEY `idx_login_time` (`login_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家登录日志表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- 商家操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `merchant_operation_log`;
CREATE TABLE `merchant_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型（商品管理/订单管理/会员管理等）',
  `operation_desc` varchar(255) NOT NULL COMMENT '操作描述',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_merchant_id` (`merchant_id`) USING BTREE,
  KEY `idx_operation_time` (`operation_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家操作日志表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- 插入默认商家账号（用户名: admin, 密码: admin123）
-- 密码使用BCrypt加密
-- ----------------------------
INSERT INTO `merchant` (`username`, `password`, `merchant_name`, `contact_person`, `contact_phone`, `email`, `status`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHCYtYdRSmFY4qyQqOqvVu', '黄色宠物商城', '管理员', '13800138000', 'admin@yellowpet.com', 1);

SET FOREIGN_KEY_CHECKS = 1;
