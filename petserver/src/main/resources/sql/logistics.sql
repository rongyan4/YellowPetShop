-- 物流信息表
CREATE TABLE IF NOT EXISTS `logistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '物流ID',
  `shipping_company` VARCHAR(100) NOT NULL COMMENT '物流公司',
  `tracking_no` VARCHAR(100) NOT NULL COMMENT '物流单号',
  `status` VARCHAR(20) NOT NULL DEFAULT 'shipped' COMMENT '物流状态：shipped-已发货，delivered-已送达',
  `remark` TEXT COMMENT '备注',
  `shipping_time` DATETIME COMMENT '发货时间',
  `delivery_time` DATETIME COMMENT '送达时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_tracking_no` (`tracking_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- 订单物流映射表
CREATE TABLE IF NOT EXISTS `order_logistics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '映射ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `logistics_id` BIGINT NOT NULL COMMENT '物流ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_logistics_id` (`logistics_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单物流映射表';
