-- 修复评论表的字段，允许为 NULL
-- 因为用户可以在商品详情页直接评论，不一定需要订单
-- parent_id 也应该可为空，因为不是所有评论都是回复

-- 修改 order_id 字段为可空
ALTER TABLE `comment` 
MODIFY COLUMN `order_id` BIGINT(20) NULL COMMENT '订单ID（可为空）';

-- 修改 parent_id 字段为可空
ALTER TABLE `comment` 
MODIFY COLUMN `parent_id` BIGINT(20) NULL COMMENT '父评论ID（可为空，用于回复评论）';

-- 查看修改后的表结构
DESCRIBE `comment`;
