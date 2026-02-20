-- ====================================
-- 智能修复脚本 - 检查后添加缺失字段
-- 使用存储过程方式，避免字段重复错误
-- ====================================

DELIMITER $$

-- 创建添加字段的存储过程
DROP PROCEDURE IF EXISTS add_column_if_not_exists$$
CREATE PROCEDURE add_column_if_not_exists(
    IN tableName VARCHAR(128),
    IN columnName VARCHAR(128),
    IN columnDefinition VARCHAR(1000)
)
BEGIN
    DECLARE column_count INT;
    
    SELECT COUNT(*) INTO column_count
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = tableName
        AND COLUMN_NAME = columnName;
    
    IF column_count = 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', tableName, '` ADD COLUMN `', columnName, '` ', columnDefinition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SELECT CONCAT('已添加字段: ', tableName, '.', columnName) AS result;
    ELSE
        SELECT CONCAT('字段已存在，跳过: ', tableName, '.', columnName) AS result;
    END IF;
END$$

DELIMITER ;

-- 为 comment 表添加字段
CALL add_column_if_not_exists('comment', 'merchant_reply', 'varchar(500) NULL DEFAULT NULL COMMENT ''商家回复'' AFTER `content`');
CALL add_column_if_not_exists('comment', 'merchant_reply_time', 'datetime NULL DEFAULT NULL COMMENT ''商家回复时间'' AFTER `merchant_reply`');

-- 为 orders 表添加字段
CALL add_column_if_not_exists('orders', 'original_amount', 'decimal(10,2) NULL DEFAULT NULL COMMENT ''原始金额（改价前）'' AFTER `total_amount`');
CALL add_column_if_not_exists('orders', 'price_modified', 'tinyint(1) NOT NULL DEFAULT 0 COMMENT ''是否已改价 1-是 0-否'' AFTER `original_amount`');
CALL add_column_if_not_exists('orders', 'shipping_company', 'varchar(50) NULL DEFAULT NULL COMMENT ''物流公司'' AFTER `shipping_status`');

-- 清理存储过程
DROP PROCEDURE IF EXISTS add_column_if_not_exists;

-- 完成提示
SELECT '数据库字段修复完成！' AS message;
