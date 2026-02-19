-- ============================================
-- 测试评论数据插入脚本
-- 功能：为商品添加测试评论和评论图片
-- ============================================

-- 确保有测试用户
INSERT INTO `user` (`id`, `username`, `password`, `email`, `nickname`, `avatar`, `status`, `role`) 
VALUES 
(1, 'testuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHdHkwVmfLsRgKy4qRrES.', 'test1@example.com', '爱宠小王', '/images/default_avatar.png', 'active', 'user'),
(2, 'testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHdHkwVmfLsRgKy4qRrES.', 'test2@example.com', '宠物达人', '/images/default_avatar.png', 'active', 'user'),
(3, 'testuser3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHdHkwVmfLsRgKy4qRrES.', 'test3@example.com', '铲屎官小李', '/images/default_avatar.png', 'active', 'user')
ON DUPLICATE KEY UPDATE 
`nickname` = VALUES(`nickname`),
`avatar` = VALUES(`avatar`),
`status` = VALUES(`status`);


-- 清空现有评论数据（可选）
-- DELETE FROM `comment_image`;
-- DELETE FROM `comment`;

-- 为商品ID=6添加评论
INSERT INTO `comment` (`commodity_id`, `user_id`, `order_id`, `parent_id`, `star`, `content`, `status`, `is_top`, `like_count`, `reply_count`, `create_time`, `update_time`) 
VALUES 
(6, 1, NULL, NULL, 5, '非常好的商品，我家宠物很喜欢吃！质量很好，会继续回购的。', 'normal', 1, 15, 2, NOW(), NOW()),
(6, 2, NULL, NULL, 4, '还不错，性价比挺高的，就是物流有点慢。', 'normal', 0, 8, 0, NOW(), NOW()),
(6, 3, NULL, NULL, 5, '味道很好，宠物吃得很香，包装也很精美。强烈推荐！', 'normal', 0, 20, 1, NOW(), NOW()),
(6, 1, NULL, NULL, 5, '第二次购买了，品质稳定，价格实惠。', 'normal', 0, 5, 0, NOW(), NOW()),
(6, 2, NULL, NULL, 4, '整体不错，就是包装可以再改进一下。', 'normal', 0, 3, 0, NOW(), NOW());

-- 为商品ID=7添加评论
INSERT INTO `comment` (`commodity_id`, `user_id`, `order_id`, `parent_id`, `star`, `content`, `status`, `is_top`, `like_count`, `reply_count`, `create_time`, `update_time`) 
VALUES 
(7, 1, NULL, NULL, 5, '超级好用，我家猫咪特别喜欢！', 'normal', 0, 12, 0, NOW(), NOW()),
(7, 3, NULL, NULL, 5, '质量很好，物流也快，满意！', 'normal', 0, 8, 0, NOW(), NOW());

-- 为商品ID=8添加评论
INSERT INTO `comment` (`commodity_id`, `user_id`, `order_id`, `parent_id`, `star`, `content`, `status`, `is_top`, `like_count`, `reply_count`, `create_time`, `update_time`) 
VALUES 
(8, 2, NULL, NULL, 5, '宝贝收到了，质量非常好，狗狗很喜欢！', 'normal', 0, 18, 1, NOW(), NOW()),
(8, 1, NULL, NULL, 4, '性价比不错，会回购的。', 'normal', 0, 6, 0, NOW(), NOW());

-- 添加评论图片（注意：这些图片路径需要在后端public/comment_image目录下存在）
-- 如果没有实际图片，可以先注释掉这部分
-- INSERT INTO `comment_image` (`comment_id`, `image_url`, `sort_order`) 
-- VALUES 
-- (1, '/comment_image/test1.jpg', 1),
-- (1, '/comment_image/test2.jpg', 2),
-- (3, '/comment_image/test3.jpg', 1),
-- (9, '/comment_image/test4.jpg', 1);

-- ============================================
-- 插入完成
-- ============================================
