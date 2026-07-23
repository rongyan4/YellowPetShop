-- ====================================
-- 最终修复脚本 - 添加缺失字段
-- 根据 petshop.sql 的标准结构添加字段
-- ====================================

-- 1. 为 comment 表添加商家回复字段（如果已存在会报错，忽略即可）
ALTER TABLE `comment` 
ADD COLUMN `merchant_reply` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商家回复' AFTER `content`;

ALTER TABLE `comment` 
ADD COLUMN `merchant_reply_time` datetime NULL DEFAULT NULL COMMENT '商家回复时间' AFTER `merchant_reply`;

-- 2. 为 orders 表添加改价相关字段（如果已存在会报错，忽略即可）
ALTER TABLE `orders` 
ADD COLUMN `original_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '原始金额（改价前）' AFTER `total_amount`;

ALTER TABLE `orders` 
ADD COLUMN `price_modified` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已改价 1-是 0-否' AFTER `original_amount`;

-- 3. 为 orders 表添加物流公司字段（如果已存在会报错，忽略即可）
ALTER TABLE `orders` 
ADD COLUMN `shipping_company` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物流公司' AFTER `shipping_status`;

-- 完成
SELECT '字段添加完成！如果某些字段报错说已存在，说明该字段已经添加过了，可以忽略。' AS message;
