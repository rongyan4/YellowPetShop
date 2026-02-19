-- 订单系统数据库脚本
-- 执行前请备份数据库

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 删除旧的订单相关表
-- ----------------------------
DROP TABLE IF EXISTS `commodity_item`;
DROP TABLE IF EXISTS `order`;

-- ----------------------------
-- 订单主表
-- ----------------------------
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_sn` varchar(64) NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `postage` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '邮费',
  `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态: PENDING-待付款, PAID-已付款, SHIPPED-已发货, COMPLETED-已完成, CANCELLED-已取消',
  `receiver_name` varchar(100) NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NULL COMMENT '收货人电话',
  `receiver_address` varchar(500) NULL COMMENT '收货地址',
  `remark` varchar(500) NULL COMMENT '订单备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `pay_time` datetime NULL COMMENT '支付时间',
  `ship_time` datetime NULL COMMENT '发货时间',
  `complete_time` datetime NULL COMMENT '完成时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_sn`(`order_sn`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 订单商品明细表
-- ----------------------------
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单商品ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `commodity_id` bigint NOT NULL COMMENT '商品ID',
  `commodity_name` varchar(255) NOT NULL COMMENT '商品名称',
  `commodity_pic` varchar(500) NULL COMMENT '商品图片',
  `commodity_price` decimal(10, 2) NOT NULL COMMENT '商品单价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_price` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE,
  INDEX `idx_commodity_id`(`commodity_id`) USING BTREE,
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_item_commodity` FOREIGN KEY (`commodity_id`) REFERENCES `commodity` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单商品明细表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
