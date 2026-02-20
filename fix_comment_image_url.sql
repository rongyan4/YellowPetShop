-- 修复 comment_image 表的 image_url 字段长度问题
-- 将 image_url 字段从 VARCHAR(500) 扩展到 TEXT 类型，以支持 base64 图片

ALTER TABLE `comment_image` 
MODIFY COLUMN `image_url` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL（支持URL和base64）';
