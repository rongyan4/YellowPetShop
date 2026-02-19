/*
 Navicat MySQL Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80400 (8.4.0)
 Source Host           : localhost:3306
 Source Schema         : petshop

 Target Server Type    : MySQL
 Target Server Version : 80400 (8.4.0)
 File Encoding         : 65001

 Date: 19/02/2026 02:37:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cart_item
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `commodity_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '商品数量',
  `checked` tinyint(1) NULL DEFAULT 1 COMMENT '是否选中 1-选中 0-未选中',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_commodity`(`user_id` ASC, `commodity_id` ASC) USING BTREE COMMENT '同一用户同一商品只能有一条记录',
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_commodity_id`(`commodity_id` ASC) USING BTREE,
  CONSTRAINT `fk_cart_commodity` FOREIGN KEY (`commodity_id`) REFERENCES `commodity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart_item
-- ----------------------------
INSERT INTO `cart_item` VALUES (11, 4, 25, 1, 1, '2026-02-14 16:23:29', '2026-02-14 16:23:29');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint(20) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT,
  `commodity_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  `parent_id` bigint NOT NULL,
  `star` tinyint NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` enum('pending','normal','hidden') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_top` tinyint(1) NOT NULL,
  `like_count` int NOT NULL,
  `reply_count` int NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_commodity_id`(`commodity_id` ASC) USING BTREE,
  INDEX `idx_commodity_id_create_time`(`commodity_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `fk_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_commodity_id` FOREIGN KEY (`commodity_id`) REFERENCES `commodity` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for comment_copy1
-- ----------------------------
DROP TABLE IF EXISTS `comment_copy1`;
CREATE TABLE `comment_copy1`  (
  `id` bigint(20) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT,
  `commodity_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  `parent_id` bigint NOT NULL,
  `star` tinyint NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` enum('pending','normal','hidden') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_top` tinyint(1) NOT NULL,
  `like_count` int NOT NULL,
  `reply_count` int NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_commodity_id`(`commodity_id` ASC) USING BTREE,
  INDEX `idx_commodity_id_create_time`(`commodity_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `fk_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `comment_copy1_ibfk_1` FOREIGN KEY (`commodity_id`) REFERENCES `commodity` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `comment_copy1_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment_copy1
-- ----------------------------

-- ----------------------------
-- Table structure for commodity
-- ----------------------------
DROP TABLE IF EXISTS `commodity`;
CREATE TABLE `commodity`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sold` int NULL DEFAULT NULL,
  `main_pic_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品详情（富文本HTML）',
  `is_valid` tinyint(1) NULL DEFAULT 1 COMMENT '是否有效 1-有效 0-无效',
  `shipping_origin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '上海' COMMENT '发货地',
  `postage` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '邮费',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of commodity
-- ----------------------------
INSERT INTO `commodity` VALUES (6, 'Peter Marshall', 343.13, '200g', 984, '/images/goods/ml.png', '', NULL, 1, '广州', 3.00);
INSERT INTO `commodity` VALUES (7, 'Tian Jialun', 214.11, '300g', 686, '/images/goods/ml.png', 'WpJ8NMGw8p', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (8, 'April Coleman', 355.96, '700g', 280, '/images/goods/ml.png', 'oErXPgZkv3', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (9, 'Esther White', 909.37, '700g', 564, '/images/goods/ml.png', 'XHs0IuY344', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (10, 'Liao Yu Ling', 115.90, '500g', 874, '/images/goods/ml.png', 'cbbkq3mwND', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (11, 'Nakamura Rena', 620.43, '300g', 461, '/images/goods/ml.png', 'CWe3AGiUcw', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (12, 'Ku Lik Sun', 696.29, '100g', 832, '/images/goods/ml.png', 'LSSaVbkTLa', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (13, 'Mo Wing Suen', 601.84, '200g', 554, '/images/goods/ml.png', '2vRfBeJFn3', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (14, 'Pan Jialun', 591.73, '700g', 464, '/images/goods/ml.png', 'uQzUIrzXdS', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (15, 'Ye Zhennan', 232.93, '300g', 120, '/images/goods/ml.png', 'Xps3IGfVn2', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (16, 'Tony Ross', 789.00, '800g', 846, '/images/goods/ml.png', '8vZP5b7JYk', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (17, 'Hasegawa Mitsuki', 408.47, '500g', 998, '/images/goods/ml.png', 'yDtJlNmT90', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (18, 'Wu Sze Yu', 468.65, '600g', 766, '/images/goods/ml.png', '36ZPB5jume', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (19, 'Lui Wing Fat', 844.98, '700g', 17, '/images/goods/ml.png', 'VE6u4JbYTz', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (20, 'Yuen Sum Wing', 922.58, '800g', 888, '/images/goods/ml.png', 'NlOoUhw1E2', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (21, 'Gao Zhennan', 799.31, '900g', 627, '/images/goods/ml.png', 'UCUNbIFxz8', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (22, 'Xiang Lu', 582.63, '700g', 683, '/images/goods/ml.png', '5HhuTSwOOp', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (23, 'Fong Wing Sze', 795.71, '600g', 130, '/images/goods/ml.png', '1sEFVTdhnH', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (24, 'Joseph Thompson', 160.60, '300g', 952, '/images/goods/ml.png', '3MosND4sQ5', NULL, 1, NULL, NULL);
INSERT INTO `commodity` VALUES (25, 'Jeffrey Tran', 330.46, '400g', 584, '/images/goods/ml.png', 'iu1Isa7AKG', NULL, 1, NULL, NULL);

-- ----------------------------
-- Table structure for commodity_image
-- ----------------------------
DROP TABLE IF EXISTS `commodity_image`;
CREATE TABLE `commodity_image`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `commodity_id` bigint NOT NULL COMMENT '商品ID',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `is_main` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否主图 1-是 0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_commodity_id`(`commodity_id` ASC) USING BTREE,
  CONSTRAINT `fk_commodity_image_commodity` FOREIGN KEY (`commodity_id`) REFERENCES `commodity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品图片表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of commodity_image
-- ----------------------------

-- ----------------------------
-- Table structure for homeswipe
-- ----------------------------
DROP TABLE IF EXISTS `homeswipe`;
CREATE TABLE `homeswipe`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id` DESC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of homeswipe
-- ----------------------------
INSERT INTO `homeswipe` VALUES (2, '/images/banner/banner2.jpg');
INSERT INTO `homeswipe` VALUES (1, '/images/banner/banner1.jpg');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单商品ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `commodity_id` bigint NOT NULL COMMENT '商品ID',
  `commodity_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `commodity_pic` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `commodity_price` decimal(10, 2) NOT NULL COMMENT '商品单价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_price` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_commodity_id`(`commodity_id` ASC) USING BTREE,
  CONSTRAINT `fk_order_item_commodity` FOREIGN KEY (`commodity_id`) REFERENCES `commodity` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单商品明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (50, 44, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 18:18:42');
INSERT INTO `order_item` VALUES (51, 45, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 18:21:36');
INSERT INTO `order_item` VALUES (52, 46, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 18:39:56');
INSERT INTO `order_item` VALUES (53, 47, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 18:40:33');
INSERT INTO `order_item` VALUES (54, 47, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 18:40:33');
INSERT INTO `order_item` VALUES (55, 48, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 18:42:15');
INSERT INTO `order_item` VALUES (56, 49, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 18:42:21');
INSERT INTO `order_item` VALUES (57, 50, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 18:43:57');
INSERT INTO `order_item` VALUES (58, 51, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 18:49:38');
INSERT INTO `order_item` VALUES (59, 52, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 18:49:51');
INSERT INTO `order_item` VALUES (60, 53, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:00:46');
INSERT INTO `order_item` VALUES (61, 54, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:06:34');
INSERT INTO `order_item` VALUES (62, 55, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:06:39');
INSERT INTO `order_item` VALUES (63, 56, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:09:05');
INSERT INTO `order_item` VALUES (64, 57, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:09:10');
INSERT INTO `order_item` VALUES (65, 58, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:09:14');
INSERT INTO `order_item` VALUES (66, 59, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:09:20');
INSERT INTO `order_item` VALUES (67, 60, 24, 'Joseph Thompson', '/images/goods/ml.png', 160.60, 1, 160.60, '2026-02-14 19:18:00');
INSERT INTO `order_item` VALUES (68, 61, 24, 'Joseph Thompson', '/images/goods/ml.png', 160.60, 1, 160.60, '2026-02-14 19:32:40');
INSERT INTO `order_item` VALUES (69, 62, 24, 'Joseph Thompson', '/images/goods/ml.png', 160.60, 1, 160.60, '2026-02-14 19:34:02');
INSERT INTO `order_item` VALUES (70, 63, 24, 'Joseph Thompson', '/images/goods/ml.png', 160.60, 1, 160.60, '2026-02-14 19:34:05');
INSERT INTO `order_item` VALUES (71, 64, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:34:40');
INSERT INTO `order_item` VALUES (72, 65, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:40:50');
INSERT INTO `order_item` VALUES (73, 66, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:56:55');
INSERT INTO `order_item` VALUES (74, 67, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:59:09');
INSERT INTO `order_item` VALUES (75, 68, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:59:11');
INSERT INTO `order_item` VALUES (76, 69, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 19:59:14');
INSERT INTO `order_item` VALUES (77, 70, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 20:00:26');
INSERT INTO `order_item` VALUES (78, 71, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 20:01:25');
INSERT INTO `order_item` VALUES (79, 72, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:21:28');
INSERT INTO `order_item` VALUES (80, 73, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:21:40');
INSERT INTO `order_item` VALUES (81, 74, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:24:46');
INSERT INTO `order_item` VALUES (82, 75, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:26:51');
INSERT INTO `order_item` VALUES (83, 76, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:30:27');
INSERT INTO `order_item` VALUES (84, 77, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:30:43');
INSERT INTO `order_item` VALUES (85, 78, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:30:55');
INSERT INTO `order_item` VALUES (86, 79, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:31:02');
INSERT INTO `order_item` VALUES (87, 80, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:31:05');
INSERT INTO `order_item` VALUES (88, 81, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:31:11');
INSERT INTO `order_item` VALUES (89, 82, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:31:38');
INSERT INTO `order_item` VALUES (90, 83, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:32:06');
INSERT INTO `order_item` VALUES (91, 84, 21, 'Gao Zhennan', '/images/goods/ml.png', 799.31, 1, 799.31, '2026-02-14 20:32:39');
INSERT INTO `order_item` VALUES (92, 85, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:33:39');
INSERT INTO `order_item` VALUES (93, 86, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:33:42');
INSERT INTO `order_item` VALUES (94, 87, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:37:12');
INSERT INTO `order_item` VALUES (95, 88, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:38:12');
INSERT INTO `order_item` VALUES (96, 89, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:49:22');
INSERT INTO `order_item` VALUES (97, 90, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 20:51:56');
INSERT INTO `order_item` VALUES (98, 91, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 21:19:26');
INSERT INTO `order_item` VALUES (99, 92, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 21:26:50');
INSERT INTO `order_item` VALUES (100, 93, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 21:27:46');
INSERT INTO `order_item` VALUES (101, 94, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 21:35:36');
INSERT INTO `order_item` VALUES (102, 95, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-14 21:35:51');
INSERT INTO `order_item` VALUES (103, 96, 15, 'Ye Zhennan', '/images/goods/ml.png', 232.93, 1, 232.93, '2026-02-15 20:36:27');
INSERT INTO `order_item` VALUES (104, 96, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-15 20:36:27');
INSERT INTO `order_item` VALUES (105, 97, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-18 18:16:37');
INSERT INTO `order_item` VALUES (106, 98, 25, 'Jeffrey Tran', '/images/goods/ml.png', 330.46, 1, 330.46, '2026-02-19 02:02:19');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_sn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `postage` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '邮费',
  `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式: WALLET-钱包 WECHAT-微信 ALIPAY-支付宝',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '订单状态: PENDING-待付款, PAID-已付款, SHIPPED-已发货, COMPLETED-已完成, CANCELLED-已取消',
  `receiver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `receiver_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货地址',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `ship_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_sn`(`order_sn` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 99 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (44, '2026021418184100011168', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 18:18:42', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (45, '2026021418213500018487', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 18:21:36', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (46, '2026021418395500010449', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 18:39:56', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (47, '2026021418403200019657', 1, 1129.77, 0.00, 1129.77, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 18:40:33', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (48, '2026021418421500014569', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 18:42:15', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (49, '2026021418422000019446', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 18:42:21', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (50, '2026021418435700016000', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 18:43:57', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (51, '2026021418493400011840', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 18:49:36', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (52, '2026021418495000015282', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 18:49:51', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (53, '2026021419004500018217', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:00:46', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (54, '2026021419063400019086', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:06:34', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (55, '2026021419063800011559', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:06:39', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (56, '2026021419090500013777', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:09:05', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (57, '2026021419091000011863', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:09:10', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (58, '2026021419091400014285', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:09:14', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (59, '2026021419092000014366', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:09:20', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (60, '2026021419175900014145', 1, 160.60, 0.00, 160.60, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:18:00', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (61, '2026021419324000015478', 1, 160.60, 0.00, 160.60, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:32:40', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (62, '2026021419340200018728', 1, 160.60, 0.00, 160.60, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:34:02', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (63, '2026021419340500012505', 1, 160.60, 0.00, 160.60, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:34:05', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (64, '2026021419344000018358', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:34:40', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (65, '2026021419404900011330', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:40:50', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (66, '2026021419565400014294', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:56:55', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (67, '2026021419590800013766', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:59:09', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (68, '2026021419591100012206', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:59:11', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (69, '2026021419591400011998', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 19:59:14', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (70, '2026021420002600013344', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:00:26', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (71, '2026021420012400012415', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:01:25', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (72, '2026021420212800016216', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:21:28', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (73, '2026021420214000015702', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:21:40', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (74, '2026021420244600011331', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:24:46', '2026-02-19 02:00:00', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (75, '2026021420265000013075', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:26:51', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (76, '2026021420302700014805', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:30:27', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (77, '2026021420304300014155', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:30:43', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (78, '2026021420305500040633', 4, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '北京市 北京市 东城区 12', '', '2026-02-14 20:30:55', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (79, '2026021420310200046581', 4, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '北京市 北京市 东城区 xx路', '', '2026-02-14 20:31:02', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (80, '2026021420310500015470', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:31:05', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (81, '2026021420311100013078', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:31:11', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (82, '2026021420313700018330', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:31:38', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (83, '2026021420320500042778', 4, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '北京市 北京市 东城区 12', '', '2026-02-14 20:32:06', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (84, '2026021420323900019204', 1, 799.31, 0.00, 799.31, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:32:39', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (85, '2026021420333800014757', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:33:39', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (86, '2026021420334200016396', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:33:42', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (87, '2026021420371100012711', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:37:12', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (88, '2026021420381200013897', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:38:12', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (89, '2026021420492200018150', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:49:22', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (90, '2026021420515600012943', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 20:51:56', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (91, '2026021421192500012858', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 21:19:26', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (92, '2026021421265000018793', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 21:26:50', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (93, '2026021421274500012596', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 21:27:46', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (94, '2026021421353500019221', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 21:35:36', '2026-02-19 02:00:01', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (95, '2026021421355100010359', 1, 330.46, 0.00, 330.46, NULL, 'COMPLETED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-14 21:35:51', '2026-02-19 01:50:37', NULL, NULL, NULL);
INSERT INTO `orders` VALUES (96, '2026021520362700014952', 1, 563.39, 0.00, 563.39, NULL, 'COMPLETED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-15 20:36:27', '2026-02-19 01:53:21', NULL, NULL, '2026-02-19 01:53:21');
INSERT INTO `orders` VALUES (97, '2026021818163700013057', 1, 330.46, 0.00, 330.46, 'WALLET', 'PAID', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-18 18:16:37', '2026-02-19 01:48:21', '2026-02-19 01:48:21', NULL, NULL);
INSERT INTO `orders` VALUES (98, '2026021902021800014928', 1, 330.46, 0.00, 330.46, NULL, 'CANCELLED', 'rongyan', '18026365451', '黑龙江省 哈尔滨市 道里区 123', '', '2026-02-19 02:02:19', '2026-02-19 02:08:00', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for payment_record
-- ----------------------------
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付方式: WALLET-钱包 WECHAT-微信 ALIPAY-支付宝',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '支付状态: PENDING-待支付 SUCCESS-成功 FAILED-失败',
  `transaction_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易流水号',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_payment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of payment_record
-- ----------------------------
INSERT INTO `payment_record` VALUES (1, 97, 1, 'WALLET', 330.46, 'SUCCESS', 'TXN17714369014670200', '2026-02-19 01:48:21', '2026-02-19 01:48:21');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '爱宠物的用户',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` enum('inactive','active','banned') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'inactive',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'user',
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'unknown',
  `birthday` date NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'rongyan', '$2a$10$7Kl9tBZlds3L.T0H6fw6o.HCewYpGiMBULaTVJPd6OypxcUOSQTxa', '26037262522@qq.com', 'rongyan', '/api/images/user/avatar/user_1_1771059822692.jpg', 'active', 'user', '男', '2004-02-28');
INSERT INTO `user` VALUES (2, 'testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHdHkwVmfLsRgKy4qRrES.', 'test2@example.com', '宠物达人', '/images/default_avatar.png', 'active', 'user', 'unknown', NULL);
INSERT INTO `user` VALUES (3, 'testuser3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHdHkwVmfLsRgKy4qRrES.', 'test3@example.com', '铲屎官小李', '/images/default_avatar.png', 'active', 'user', 'unknown', NULL);
INSERT INTO `user` VALUES (4, 'rongyan1', '$2a$10$O7L2TTCBXFQ9c6jS9tnpvOLotreE18G5RHgt7p6mhcF4BpBgr3BW.', '2603726251@qq.com', 'rongyan1', NULL, 'active', 'user', 'unknown', NULL);

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区/县',
  `detail_address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址 1-是 0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (1, 1, '张三', '13800138000', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城A座1001室', 0, '2026-02-14 11:53:28', '2026-02-14 17:14:54');
INSERT INTO `user_address` VALUES (2, 1, '李四', '13900139000', '上海市', '上海市', '浦东新区', '世纪大道1号国金中心2号楼2001室', 0, '2026-02-14 11:53:28', '2026-02-14 11:53:28');
INSERT INTO `user_address` VALUES (3, 1, 'rongyan', '18026365451', '黑龙江省', '哈尔滨市', '道里区', '123', 1, '2026-02-14 17:14:54', '2026-02-14 17:14:54');
INSERT INTO `user_address` VALUES (4, 4, 'rongyan', '18026365451', '北京市', '北京市', '东城区', 'xx路', 0, '2026-02-14 20:28:46', '2026-02-14 20:28:46');
INSERT INTO `user_address` VALUES (5, 4, 'rongyan', '18026365451', '北京市', '北京市', '东城区', '12', 0, '2026-02-14 20:29:07', '2026-02-14 20:29:07');

-- ----------------------------
-- Table structure for user_browse_history
-- ----------------------------
DROP TABLE IF EXISTS `user_browse_history`;
CREATE TABLE `user_browse_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `commodity_id` bigint NOT NULL COMMENT '商品ID',
  `browse_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_commodity_id`(`commodity_id` ASC) USING BTREE,
  INDEX `idx_browse_time`(`browse_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户浏览记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_browse_history
-- ----------------------------
INSERT INTO `user_browse_history` VALUES (1, 1, 25, '2026-02-19 01:44:25');
INSERT INTO `user_browse_history` VALUES (2, 1, 20, '2026-02-14 18:15:23');
INSERT INTO `user_browse_history` VALUES (3, 4, 25, '2026-02-14 20:32:10');
INSERT INTO `user_browse_history` VALUES (4, 1, 21, '2026-02-18 18:42:08');
INSERT INTO `user_browse_history` VALUES (5, 1, 23, '2026-02-14 18:16:15');
INSERT INTO `user_browse_history` VALUES (6, 1, 8, '2026-02-14 18:15:51');
INSERT INTO `user_browse_history` VALUES (7, 1, 24, '2026-02-14 19:17:54');
INSERT INTO `user_browse_history` VALUES (8, 1, 15, '2026-02-15 20:36:20');

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `commodity_id` bigint NOT NULL COMMENT '商品ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_commodity`(`user_id` ASC, `commodity_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_commodity_id`(`commodity_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorite
-- ----------------------------

-- ----------------------------
-- Table structure for user_wallet
-- ----------------------------
DROP TABLE IF EXISTS `user_wallet`;
CREATE TABLE `user_wallet`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
  `pay_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付密码（加密存储）',
  `is_locked` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否锁定 1-锁定 0-正常',
  `lock_time` datetime NULL DEFAULT NULL COMMENT '锁定时间',
  `error_count` int NOT NULL DEFAULT 0 COMMENT '密码错误次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_wallet_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户钱包表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_wallet
-- ----------------------------
INSERT INTO `user_wallet` VALUES (1, 1, 9999668.54, '$2a$10$5StsPMQO6G0VSPtOVqUhOet8jFhehq7jRZ7Oybdbk18skuvVHJRfq', 0, '2026-02-14 12:55:00', 0, '2026-02-14 11:53:28', '2026-02-19 01:48:21');
INSERT INTO `user_wallet` VALUES (2, 4, 0.00, NULL, 0, NULL, 0, '2026-02-14 16:23:53', '2026-02-14 16:23:53');

-- ----------------------------
-- Table structure for wallet_transaction
-- ----------------------------
DROP TABLE IF EXISTS `wallet_transaction`;
CREATE TABLE `wallet_transaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '关联订单ID',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '交易类型: RECHARGE-充值 PAYMENT-支付 REFUND-退款',
  `amount` decimal(10, 2) NOT NULL COMMENT '交易金额',
  `balance_before` decimal(10, 2) NOT NULL COMMENT '交易前余额',
  `balance_after` decimal(10, 2) NOT NULL COMMENT '交易后余额',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  CONSTRAINT `fk_transaction_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '钱包流水表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wallet_transaction
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
