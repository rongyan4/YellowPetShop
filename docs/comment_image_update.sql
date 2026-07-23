-- 评论图片功能数据库更新脚本
-- 用于支持评论图片上传到static目录

-- 1. 确保comment_image表存在
CREATE TABLE IF NOT EXISTS `comment_image` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL路径',
  `sort_order` int DEFAULT 0 COMMENT '排序顺序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论图片表';

-- 2. 确保comment表的order_id字段可以为NULL（支持非订单评论）
ALTER TABLE `comment` MODIFY COLUMN `order_id` bigint NULL COMMENT '订单ID（可为空）';

-- 3. 添加索引优化查询性能
ALTER TABLE `comment` ADD INDEX IF NOT EXISTS `idx_order_commodity_user` (`order_id`, `commodity_id`, `user_id`);
ALTER TABLE `comment` ADD INDEX IF NOT EXISTS `idx_commodity_status` (`commodity_id`, `status`);

-- 完成
SELECT '评论图片功能数据库更新完成' AS message;
