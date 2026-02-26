# 评论图片存储目录

此目录用于存储用户上传的评论图片。

## 目录结构
```
static/images/comment/
├── comment_1_20260222120000_1234.jpg
├── comment_1_20260222120001_5678.png
└── ...
```

## 文件命名规则
格式：`comment_{userId}_{timestamp}_{random}.{extension}`

- `userId`: 用户ID
- `timestamp`: 时间戳（yyyyMMddHHmmss）
- `random`: 4位随机数
- `extension`: 文件扩展名（jpg/png/gif/webp）

## 访问路径
前端访问路径：`/api/images/comment/{filename}`

例如：`/api/images/comment/comment_1_20260222120000_1234.jpg`

## 注意事项
1. 图片大小限制：5MB
2. 支持格式：jpg、png、gif、webp
3. 每条评论最多上传6张图片
4. 图片会自动保存到此目录
