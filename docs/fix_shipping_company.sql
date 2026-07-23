-- 修复 orders 表缺少 shipping_company 字段的问题
-- 日期: 2026-02-20

USE petshop;

-- 添加 shipping_company 字段到 orders 表
ALTER TABLE `orders` 
ADD COLUMN `shipping_company` VARCHAR(100) NULL DEFAULT NULL COMMENT '物流公司' 
AFTER `shipping_status`;

-- 验证字段是否添加成功
SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'petshop' 
  AND TABLE_NAME = 'orders' 
  AND COLUMN_NAME = 'shipping_company';
