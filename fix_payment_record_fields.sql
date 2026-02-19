-- 确保 payment_record 表结构正确
-- 如果字段名不匹配，执行以下修改

-- 检查并修改字段名
ALTER TABLE payment_record CHANGE COLUMN payment_amount amount DECIMAL(10, 2) NOT NULL COMMENT '支付金额';
ALTER TABLE payment_record CHANGE COLUMN payment_status status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '支付状态: PENDING-待支付 SUCCESS-成功 FAILED-失败';
ALTER TABLE payment_record CHANGE COLUMN transaction_id transaction_no VARCHAR(100) NULL DEFAULT NULL COMMENT '交易流水号';
ALTER TABLE payment_record CHANGE COLUMN payment_time pay_time DATETIME NULL DEFAULT NULL COMMENT '支付时间';

-- 删除 update_time 字段（如果存在）
ALTER TABLE payment_record DROP COLUMN IF EXISTS update_time;
