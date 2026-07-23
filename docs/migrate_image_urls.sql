-- =============================================
-- 文件上传URL迁移脚本
npm -- 用于将完整URL更新为相对路径
-- =============================================

-- 使用说明：
-- 1. 此脚本将完整URL转换为相对路径
-- 2. 例如：http://localhost:3000/api/images/goods/xxx.jpg -> /api/images/goods/xxx.jpg
-- 3. 执行此脚本前请先备份数据库！

-- =============================================
-- 1. 更新商品表图片URL（转换为相对路径）
-- =============================================
-- 更新主图
UPDATE commodity 
SET image_url = CONCAT('/api/images/', SUBSTRING_INDEX(image_url, '/api/images/', -1))
WHERE image_url IS NOT NULL 
  AND image_url != ''
  AND (image_url LIKE 'http://%' OR image_url LIKE 'https://%');

-- 更新详情图
UPDATE commodity 
SET detail_images = CONCAT('/api/images/', SUBSTRING_INDEX(detail_images, '/api/images/', -1))
WHERE detail_images IS NOT NULL 
  AND detail_images != ''
  AND (detail_images LIKE 'http://%' OR detail_images LIKE 'https://%');

-- =============================================
-- 2. 更新用户表头像URL（转换为相对路径）
-- =============================================
UPDATE user 
SET avatar_url = CONCAT('/api/images/', SUBSTRING_INDEX(avatar_url, '/api/images/', -1))
WHERE avatar_url IS NOT NULL 
  AND avatar_url != ''
  AND (avatar_url LIKE 'http://%' OR avatar_url LIKE 'https://%');

-- =============================================
-- 3. 更新评论表图片URL（转换为相对路径）
-- =============================================
UPDATE comment 
SET image_url = CONCAT('/api/images/', SUBSTRING_INDEX(image_url, '/api/images/', -1))
WHERE image_url IS NOT NULL 
  AND image_url != ''
  AND (image_url LIKE 'http://%' OR image_url LIKE 'https://%');

-- =============================================
-- 4. 更新分类表图片URL（如果有）
-- =============================================
UPDATE category 
SET image_url = CONCAT('/api/images/', SUBSTRING_INDEX(image_url, '/api/images/', -1))
WHERE image_url IS NOT NULL 
  AND image_url != ''
  AND (image_url LIKE 'http://%' OR image_url LIKE 'https://%');

-- =============================================
-- 5. 更新轮播图表图片URL（如果有）
-- =============================================
UPDATE swipe 
SET image_url = CONCAT('/api/images/', SUBSTRING_INDEX(image_url, '/api/images/', -1))
WHERE image_url IS NOT NULL 
  AND image_url != ''
  AND (image_url LIKE 'http://%' OR image_url LIKE 'https://%');

-- =============================================
-- 验证更新结果
-- =============================================
SELECT '商品图片URL示例:' as '检查项', image_url FROM commodity WHERE image_url IS NOT NULL LIMIT 3;
SELECT '用户头像URL示例:' as '检查项', avatar_url FROM user WHERE avatar_url IS NOT NULL LIMIT 3;
SELECT '评论图片URL示例:' as '检查项', image_url FROM comment WHERE image_url IS NOT NULL LIMIT 3;

-- =============================================
-- 统计信息
-- =============================================
SELECT 
    '商品表' as '表名',
    COUNT(*) as '总记录数',
    SUM(CASE WHEN image_url LIKE '/api/images/%' THEN 1 ELSE 0 END) as '相对路径记录数',
    SUM(CASE WHEN image_url LIKE 'http%' THEN 1 ELSE 0 END) as '完整URL记录数'
FROM commodity;

SELECT 
    '用户表' as '表名',
    COUNT(*) as '总记录数',
    SUM(CASE WHEN avatar_url LIKE '/api/images/%' THEN 1 ELSE 0 END) as '相对路径记录数',
    SUM(CASE WHEN avatar_url LIKE 'http%' THEN 1 ELSE 0 END) as '完整URL记录数'
FROM user;

SELECT 
    '评论表' as '表名',
    COUNT(*) as '总记录数',
    SUM(CASE WHEN image_url LIKE '/api/images/%' THEN 1 ELSE 0 END) as '相对路径记录数',
    SUM(CASE WHEN image_url LIKE 'http%' THEN 1 ELSE 0 END) as '完整URL记录数'
FROM comment;

-- =============================================
-- 说明
-- =============================================
-- 转换后的URL格式：/api/images/goods/goods_1_20260224120000_1234.jpg
-- 前端会自动拼接域名：http://localhost:3000/api/images/goods/goods_1_20260224120000_1234.jpg
-- 生产环境会拼接生产域名：https://your-domain.com/api/images/goods/goods_1_20260224120000_1234.jpg
