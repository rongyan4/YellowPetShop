-- 为 user 表增加积分字段，用于会员等级计算
ALTER TABLE `user`
  ADD COLUMN `points` int NOT NULL DEFAULT 0 COMMENT '积分' AFTER `phone`;

-- 如已在数据库中手动添加该字段，可忽略本脚本

