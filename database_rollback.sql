-- ============================================
-- 数据库回滚脚本
-- 功能：撤销数据库升级，恢复到升级前状态
-- 创建时间：2026-02-02
-- 警告：执行此脚本将删除升级后添加的所有数据！
-- ============================================

-- 1. 删除测试评论图片数据
DELETE FROM `comment_image` WHERE `comment_id` IN (1, 3);

-- 2. 删除测试评论数据
DELETE FROM `comment` WHERE `commodity_id` IN (6, 7) AND `user_id` = 1;

-- 3. 删除测试商品图片数据
DELETE FROM `commodity_image` WHERE `commodity_id` IN (6, 7, 8);

-- 4. 删除评论图片表
DROP TABLE IF EXISTS `comment_image`;

-- 5. 删除商品图片表
DROP TABLE IF EXISTS `commodity_image`;

-- 6. 恢复购物车表到原始结构
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
  `id` BIGINT NOT NULL,
  `commodity_item_id` BIGINT NULL DEFAULT NULL,
  `checked` TINYINT(1) NULL DEFAULT NULL,
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- 重新添加原始外键约束
ALTER TABLE `cart_item` ADD CONSTRAINT `pk_xommodity_item_id` FOREIGN KEY (`commodity_item_id`) REFERENCES `commodity_item` (`id`);

-- 7. 恢复 comment 表字段（移除默认值和注释）
ALTER TABLE `comment`
  MODIFY COLUMN `order_id` BIGINT NOT NULL,
  MODIFY COLUMN `parent_id` BIGINT NOT NULL,
  MODIFY COLUMN `star` TINYINT NOT NULL,
  MODIFY COLUMN `status` ENUM('pending','normal','hidden') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  MODIFY COLUMN `is_top` TINYINT(1) NOT NULL,
  MODIFY COLUMN `like_count` INT NOT NULL,
  MODIFY COLUMN `reply_count` INT NOT NULL;

-- 8. 恢复 commodity 表字段（移除默认值和精度）
ALTER TABLE `commodity`
  MODIFY COLUMN `postage` DECIMAL NULL,
  MODIFY COLUMN `is_valid` TINYINT(1) NULL DEFAULT NULL,
  MODIFY COLUMN `shipping_origin` VARCHAR(255) NULL,
  MODIFY COLUMN `detail` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL;

-- ============================================
-- 回滚完成
-- 警告：所有升级后的数据已被删除！
-- ============================================
