-- ====================================
-- 数据库结构补充脚本
-- 用于添加缺失的表结构
-- 创建时间: 2026-02-20
-- ====================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 评论图片表（如果不存在则创建）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `comment_image` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `comment_id` bigint(20) UNSIGNED ZEROFILL NOT NULL COMMENT '评论ID',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_comment_id` (`comment_id`) USING BTREE,
  CONSTRAINT `fk_comment_image_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论图片表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- 检查并添加 commodity 表的 category_id 字段（如果不存在）
-- ----------------------------
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'commodity' 
    AND COLUMN_NAME = 'category_id'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `commodity` ADD COLUMN `category_id` bigint NULL DEFAULT NULL COMMENT ''分类ID'' AFTER `id`',
  'SELECT ''category_id already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------
-- 添加 commodity 表的外键约束（如果不存在）
-- ----------------------------
SET @fk_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'commodity' 
    AND CONSTRAINT_NAME = 'fk_commodity_category'
);

SET @sql = IF(@fk_exists = 0, 
  'ALTER TABLE `commodity` ADD CONSTRAINT `fk_commodity_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL ON UPDATE CASCADE',
  'SELECT ''fk_commodity_category already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------
-- 添加 commodity 表的索引（如果不存在）
-- ----------------------------
SET @idx_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.STATISTICS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'commodity' 
    AND INDEX_NAME = 'idx_category_id'
);

SET @sql = IF(@idx_exists = 0, 
  'ALTER TABLE `commodity` ADD INDEX `idx_category_id` (`category_id`) USING BTREE',
  'SELECT ''idx_category_id already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------
-- 确保 category 表存在
-- ----------------------------
CREATE TABLE IF NOT EXISTS `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类图标',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- 插入默认分类数据（如果不存在）
-- ----------------------------
INSERT IGNORE INTO `category` (`id`, `name`, `icon`, `sort_order`) VALUES
(1, '猫粮', '/images/category/cat-food.png', 1),
(2, '狗粮', '/images/category/dog-food.png', 2),
(3, '玩具', '/images/category/toy.png', 3),
(4, '用品', '/images/category/supplies.png', 4),
(5, '医疗保健', '/images/category/health.png', 5),
(6, '其他', '/images/category/other.png', 6);

-- ----------------------------
-- 确保 merchant 表存在
-- ----------------------------
CREATE TABLE IF NOT EXISTS `merchant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商家ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商家账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `merchant_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商家名称',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- 确保 merchant_login_log 表存在
-- ----------------------------
CREATE TABLE IF NOT EXISTS `merchant_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器信息',
  `login_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '登录状态 1-成功 0-失败',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_merchant_id` (`merchant_id`) USING BTREE,
  INDEX `idx_login_time` (`login_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家登录日志表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- 确保 merchant_operation_log 表存在
-- ----------------------------
CREATE TABLE IF NOT EXISTS `merchant_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `merchant_id` bigint NOT NULL COMMENT '商家ID',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型（商品管理/订单管理/会员管理等）',
  `operation_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作描述',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_merchant_id` (`merchant_id`) USING BTREE,
  INDEX `idx_operation_time` (`operation_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家操作日志表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- 插入默认商家账号（如果不存在）
-- 用户名: admin, 密码: admin123
-- ----------------------------
INSERT IGNORE INTO `merchant` (`id`, `username`, `password`, `merchant_name`, `contact_person`, `contact_phone`, `email`, `status`) 
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHCYtYdRSmFY4qyQqOqvVu', '黄色宠物商城', '管理员', '13800138000', 'admin@yellowpet.com', 1);

-- ----------------------------
-- 为 orders 表添加物流相关字段（如果不存在）
-- ----------------------------
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'orders' 
    AND COLUMN_NAME = 'shipping_status'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `orders` ADD COLUMN `shipping_status` varchar(20) NULL DEFAULT NULL COMMENT ''物流状态'' AFTER `remark`',
  'SELECT ''shipping_status already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 tracking_no 字段
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'orders' 
    AND COLUMN_NAME = 'tracking_no'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `orders` ADD COLUMN `tracking_no` varchar(100) NULL DEFAULT NULL COMMENT ''物流单号'' AFTER `shipping_status`',
  'SELECT ''tracking_no already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 shipping_time 字段
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'orders' 
    AND COLUMN_NAME = 'shipping_time'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `orders` ADD COLUMN `shipping_time` datetime NULL DEFAULT NULL COMMENT ''发货时间'' AFTER `tracking_no`',
  'SELECT ''shipping_time already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 cancel_time 字段
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'orders' 
    AND COLUMN_NAME = 'cancel_time'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `orders` ADD COLUMN `cancel_time` datetime NULL DEFAULT NULL COMMENT ''取消时间'' AFTER `shipping_time`',
  'SELECT ''cancel_time already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------
-- 为 user 表添加缺失字段（如果不存在）
-- ----------------------------
-- 添加 phone 字段
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'user' 
    AND COLUMN_NAME = 'phone'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `user` ADD COLUMN `phone` varchar(20) NULL DEFAULT NULL COMMENT ''手机号'' AFTER `birthday`',
  'SELECT ''phone already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 is_valid 字段
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'user' 
    AND COLUMN_NAME = 'is_valid'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `user` ADD COLUMN `is_valid` tinyint(1) NOT NULL DEFAULT 1 COMMENT ''是否有效 1-有效 0-无效'' AFTER `phone`',
  'SELECT ''is_valid already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 create_time 字段
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'user' 
    AND COLUMN_NAME = 'create_time'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `user` ADD COLUMN `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'' AFTER `is_valid`',
  'SELECT ''create_time already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 update_time 字段
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'user' 
    AND COLUMN_NAME = 'update_time'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `user` ADD COLUMN `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER `create_time`',
  'SELECT ''update_time already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------
-- 为 commodity 表添加销量字段（如果不存在）
-- ----------------------------
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'commodity' 
    AND COLUMN_NAME = 'sold'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `commodity` ADD COLUMN `sold` int NOT NULL DEFAULT 0 COMMENT ''已售数量'' AFTER `unit`',
  'SELECT ''sold already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------
-- 为 comment 表添加商家回复字段（如果不存在）
-- ----------------------------
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'comment' 
    AND COLUMN_NAME = 'merchant_reply'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `comment` ADD COLUMN `merchant_reply` varchar(500) NULL DEFAULT NULL COMMENT ''商家回复'' AFTER `content`',
  'SELECT ''merchant_reply already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 merchant_reply_time 字段
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'comment' 
    AND COLUMN_NAME = 'merchant_reply_time'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `comment` ADD COLUMN `merchant_reply_time` datetime NULL DEFAULT NULL COMMENT ''商家回复时间'' AFTER `merchant_reply`',
  'SELECT ''merchant_reply_time already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------
-- 为 orders 表添加原价字段（用于改价功能）
-- ----------------------------
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'orders' 
    AND COLUMN_NAME = 'original_amount'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `orders` ADD COLUMN `original_amount` decimal(10,2) NULL DEFAULT NULL COMMENT ''原始金额（改价前）'' AFTER `total_amount`',
  'SELECT ''original_amount already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 price_modified 字段
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'orders' 
    AND COLUMN_NAME = 'price_modified'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `orders` ADD COLUMN `price_modified` tinyint(1) NOT NULL DEFAULT 0 COMMENT ''是否已改价 1-是 0-否'' AFTER `original_amount`',
  'SELECT ''price_modified already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET FOREIGN_KEY_CHECKS = 1;

-- ====================================
-- 执行完成提示
-- ====================================
SELECT '数据库结构补充完成！' AS message;
