-- ====================================
-- 快速修复脚本 - 仅添加缺失字段
-- 用于修复当前报错的数据库字段问题
-- ====================================

-- 为 comment 表添加商家回复字段（如果字段已存在会报错，请忽略）
ALTER TABLE `comment` ADD COLUMN `merchant_reply` varchar(500) NULL DEFAULT NULL COMMENT '商家回复' AFTER `content`;
ALTER TABLE `comment` ADD COLUMN `merchant_reply_time` datetime NULL DEFAULT NULL COMMENT '商家回复时间' AFTER `merchant_reply`;

-- 为 orders 表添加改价相关字段（如果字段已存在会报错，请忽略）
ALTER TABLE `orders` ADD COLUMN `original_amount` decimal(10,2) NULL DEFAULT NULL COMMENT '原始金额（改价前）' AFTER `total_amount`;
ALTER TABLE `orders` ADD COLUMN `price_modified` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已改价 1-是 0-否' AFTER `original_amount`;

-- 为 orders 表添加物流公司字段（如果字段已存在会报错，请忽略）
ALTER TABLE `orders` ADD COLUMN `shipping_company` varchar(50) NULL DEFAULT NULL COMMENT '物流公司' AFTER `shipping_status`;

-- 验证字段是否添加成功
SELECT '修复完成！' AS message;
