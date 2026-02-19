-- ============================================
-- 数据库升级脚本
-- 功能：完善商品详情、购物车、评论功能
-- 创建时间：2026-02-02
-- ============================================

-- 1. 修改 commodity 表，完善字段
ALTER TABLE `commodity` 
  MODIFY COLUMN `postage` DECIMAL(10, 2) NULL DEFAULT 0.00 COMMENT '邮费',
  MODIFY COLUMN `is_valid` TINYINT(1) NULL DEFAULT 1 COMMENT '是否有效 1-有效 0-无效',
  MODIFY COLUMN `shipping_origin` VARCHAR(255) NULL DEFAULT '未知' COMMENT '发货地',
  MODIFY COLUMN `detail` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品详情（富文本HTML）';

-- 2. 创建商品图片表（支持多图轮播）
CREATE TABLE IF NOT EXISTS `commodity_image` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `commodity_id` BIGINT NOT NULL COMMENT '商品ID',
  `image_url` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `is_main` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否主图 1-是 0-否',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_commodity_id` (`commodity_id` ASC) USING BTREE,
  CONSTRAINT `fk_commodity_image_commodity` FOREIGN KEY (`commodity_id`) REFERENCES `commodity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品图片表' ROW_FORMAT = Dynamic;

-- 3. 重新设计购物车表（直接关联商品和用户）
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `commodity_id` BIGINT NOT NULL COMMENT '商品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '商品数量',
  `checked` TINYINT(1) NULL DEFAULT 1 COMMENT '是否选中 1-选中 0-未选中',
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id` ASC) USING BTREE,
  INDEX `idx_commodity_id` (`commodity_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_commodity` (`user_id`, `commodity_id`) USING BTREE COMMENT '同一用户同一商品只能有一条记录',
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cart_commodity` FOREIGN KEY (`commodity_id`) REFERENCES `commodity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- 4. 创建评论图片表
CREATE TABLE IF NOT EXISTS `comment_image` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论图片ID',
  `comment_id` BIGINT NOT NULL COMMENT '评论ID',
  `image_url` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_comment_id` (`comment_id` ASC) USING BTREE,
  CONSTRAINT `fk_comment_image_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论图片表' ROW_FORMAT = Dynamic;

-- 5. 修改 comment 表，优化字段
ALTER TABLE `comment`
  MODIFY COLUMN `order_id` BIGINT NULL DEFAULT NULL COMMENT '订单ID（可为空，允许未购买用户评论）',
  MODIFY COLUMN `parent_id` BIGINT NULL DEFAULT NULL COMMENT '父评论ID（回复评论时使用）',
  MODIFY COLUMN `star` TINYINT NOT NULL DEFAULT 5 COMMENT '评分 1-5星',
  MODIFY COLUMN `status` ENUM('pending','normal','hidden') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待审核 normal-正常 hidden-隐藏',
  MODIFY COLUMN `is_top` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否置顶 1-是 0-否',
  MODIFY COLUMN `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  MODIFY COLUMN `reply_count` INT NOT NULL DEFAULT 0 COMMENT '回复数';

-- 6. 为现有商品添加默认数据
UPDATE `commodity` SET 
  `shipping_origin` = '上海',
  `postage` = 0.00,
  `is_valid` = 1,
  `detail` = '<div><h3>商品详情</h3><p>这是一款优质的宠物商品，精心挑选原材料，为您的爱宠提供最好的呵护。</p></div>'
WHERE `shipping_origin` IS NULL OR `postage` IS NULL OR `is_valid` IS NULL;

-- 7. 插入测试商品图片数据（为前几个商品添加）
INSERT INTO `commodity_image` (`commodity_id`, `image_url`, `sort_order`, `is_main`) VALUES
(6, '/images/goods/ml.png', 1, 1),
(6, '/images/goods/ml.png', 2, 0),
(7, '/images/goods/ml.png', 1, 1),
(7, '/images/goods/ml.png', 2, 0),
(8, '/images/goods/ml.png', 1, 1),
(8, '/images/goods/ml.png', 2, 0);

-- 8. 插入测试评论数据
INSERT INTO `comment` (`commodity_id`, `user_id`, `order_id`, `parent_id`, `star`, `content`, `status`, `is_top`, `like_count`, `reply_count`, `create_time`, `update_time`) VALUES
(6, 1, NULL, NULL, 5, '非常好的商品，我家宠物很喜欢吃！质量很好，会继续回购的。', 'normal', 0, 15, 2, NOW(), NOW()),
(6, 1, NULL, NULL, 4, '还不错，性价比挺高的，就是物流有点慢。', 'normal', 0, 8, 0, NOW(), NOW()),
(7, 1, NULL, NULL, 5, '味道很好，宠物吃得很香，包装也很精美。', 'normal', 0, 20, 1, NOW(), NOW());

-- 9. 插入测试评论图片数据
INSERT INTO `comment_image` (`comment_id`, `image_url`, `sort_order`) VALUES
(1, '/images/goods/ml.png', 1),
(1, '/images/goods/ml.png', 2),
(3, '/images/goods/ml.png', 1);

-- ============================================
-- 升级完成
-- ============================================
