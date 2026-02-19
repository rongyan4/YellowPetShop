-- 商品分类系统更新脚本

-- 1. 创建商品分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类图标',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = DYNAMIC;

-- 2. 插入分类数据
INSERT INTO `category` (`name`, `icon`, `sort_order`) VALUES
('狗粮', 'pets-o', 1),
('猫粮', 'smile-o', 2),
('零食', 'gift-o', 3),
('玩具', 'like-o', 4),
('用品', 'bag-o', 5),
('保健品', 'medal-o', 6);

-- 3. 修改商品表，添加分类字段
ALTER TABLE `commodity` ADD COLUMN `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID' AFTER `id`;
ALTER TABLE `commodity` ADD INDEX `idx_category_id`(`category_id` ASC) USING BTREE;
ALTER TABLE `commodity` ADD CONSTRAINT `fk_commodity_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- 4. 为现有商品随机分配分类
-- 获取分类ID范围（假设是1-6）
UPDATE `commodity` SET `category_id` = FLOOR(1 + RAND() * 6) WHERE `category_id` IS NULL;

-- 查看分类分布
SELECT c.name AS '分类名称', COUNT(co.id) AS '商品数量'
FROM category c
LEFT JOIN commodity co ON c.id = co.category_id
GROUP BY c.id, c.name
ORDER BY c.sort_order;
