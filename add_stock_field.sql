-- 为商品表添加库存字段
ALTER TABLE `commodity` ADD COLUMN `stock` int NOT NULL DEFAULT 0 COMMENT '库存数量' AFTER `sold`;

-- 为现有商品设置默认库存
UPDATE `commodity` SET `stock` = 999 WHERE `stock` = 0;
