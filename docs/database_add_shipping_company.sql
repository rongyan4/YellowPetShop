-- ====================================
-- 添加物流公司字段
-- 创建时间: 2026-02-20
-- ====================================

SET NAMES utf8mb4;

-- ----------------------------
-- 为 orders 表添加 shipping_company 字段（如果不存在）
-- ----------------------------
SET @column_exists = (
  SELECT COUNT(*) 
  FROM INFORMATION_SCHEMA.COLUMNS 
  WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'orders' 
    AND COLUMN_NAME = 'shipping_company'
);

SET @sql = IF(@column_exists = 0, 
  'ALTER TABLE `orders` ADD COLUMN `shipping_company` varchar(100) NULL DEFAULT NULL COMMENT ''物流公司'' AFTER `shipping_status`',
  'SELECT ''shipping_company already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT 'shipping_company字段添加完成' AS result;
