-- 修复 commodity 表中 is_valid 字段为 NULL 的问题
-- 将所有 NULL 值更新为 1（有效）

UPDATE commodity 
SET is_valid = 1 
WHERE is_valid IS NULL;

-- 确保 is_valid 字段不允许为 NULL
ALTER TABLE commodity 
MODIFY COLUMN is_valid tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效 1-有效 0-无效';
