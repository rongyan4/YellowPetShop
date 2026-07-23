-- 宠物档案系统数据库表（商品推荐版）
-- 创建时间：2026-02-26

-- 1. 宠物档案主表
CREATE TABLE IF NOT EXISTS pet_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '档案ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  
  -- 基本信息（必填）
  pet_name VARCHAR(50) NOT NULL COMMENT '宠物名称',
  pet_type VARCHAR(20) NOT NULL COMMENT '宠物类型：cat/dog/other',
  age_stage VARCHAR(20) NOT NULL COMMENT '年龄段：puppy/adult/senior',
  
  -- 基本信息（可选）
  body_size VARCHAR(20) COMMENT '体型：small/medium/large',
  gender VARCHAR(10) COMMENT '性别：male/female',
  avatar_url VARCHAR(255) COMMENT '头像URL',
  
  -- 体质特征（商品推荐核心字段）
  is_shedding TINYINT(1) DEFAULT 0 COMMENT '易掉毛体质',
  is_skin_sensitive TINYINT(1) DEFAULT 0 COMMENT '皮肤敏感',
  is_stomach_sensitive TINYINT(1) DEFAULT 0 COMMENT '肠胃敏感',
  has_dental_issue TINYINT(1) DEFAULT 0 COMMENT '口腔问题',
  has_joint_issue TINYINT(1) DEFAULT 0 COMMENT '关节问题',
  has_tear_stain TINYINT(1) DEFAULT 0 COMMENT '泪痕问题',
  is_overweight TINYINT(1) DEFAULT 0 COMMENT '肥胖倾向',
  is_picky_eater TINYINT(1) DEFAULT 0 COMMENT '挑食',
  
  -- 生活习惯（可选）
  activity_level VARCHAR(20) COMMENT '活动量：low/medium/high',
  food_preference VARCHAR(20) COMMENT '饮食偏好：dry/wet/mixed',
  remark VARCHAR(500) COMMENT '备注',
  
  -- 系统字段
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除：0否1是',
  
  INDEX idx_user_id (user_id),
  INDEX idx_shedding (is_shedding),
  INDEX idx_skin_sensitive (is_skin_sensitive),
  INDEX idx_stomach_sensitive (is_stomach_sensitive),
  INDEX idx_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案表（商品推荐版）';

-- 插入测试数据
INSERT INTO pet_profile (user_id, pet_name, pet_type, age_stage, body_size, gender, 
  is_shedding, is_skin_sensitive, is_stomach_sensitive, has_dental_issue, 
  has_joint_issue, has_tear_stain, is_overweight, is_picky_eater,
  activity_level, food_preference, remark) 
VALUES 
  (1, '小橘', 'cat', 'adult', 'medium', 'male', 
   1, 0, 1, 0, 0, 1, 0, 0, 
   'medium', 'mixed', '喜欢吃鱼味的猫粮'),
  (1, '大黄', 'dog', 'senior', 'large', 'male', 
   1, 1, 0, 1, 1, 0, 1, 0, 
   'low', 'dry', '老年犬，需要关节保健'),
  (2, '咪咪', 'cat', 'puppy', 'small', 'female', 
   0, 0, 0, 0, 0, 0, 0, 1, 
   'high', 'wet', '幼猫，比较挑食');
