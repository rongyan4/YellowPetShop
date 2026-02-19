# 头像上传功能实现总结

## ✅ 已完成的后端实现

### 1. UserController.java - 上传接口

**接口路径**: `POST /api/user/upload_avatar`

**主要功能**:
- ✅ 接收 MultipartFile 文件参数
- ✅ 验证文件是否为空
- ✅ 验证文件类型（image/jpeg, image/png, image/gif, image/webp）
- ✅ 验证文件大小（最大 2MB）
- ✅ 生成唯一文件名：`user_{userId}_{timestamp}.{extension}`
- ✅ 保存到 `src/main/resources/static/images/user/avatar/`
- ✅ 删除用户旧头像（避免文件堆积）
- ✅ 更新数据库中的 avatar 字段
- ✅ 返回头像 URL 和上传信息

**文件命名规则**:
```
user_1_1640000000000.jpg
user_2_1640000000001.png
```

**返回数据格式**:
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "avatarUrl": "/images/user/avatar/user_1_1640000000000.jpg",
    "fileName": "user_1_1640000000000.jpg",
    "fileSize": 102400,
    "uploadTime": "2024-01-20T10:30:00"
  }
}
```

### 2. UserService.java - 服务接口

新增方法:
```java
public void updateAvatar(Long userId, String avatarUrl);
```

### 3. UserServiceImpl.java - 服务实现

**updateAvatar 方法**:
- 根据 userId 查询用户
- 更新用户的 avatar 字段
- 保存到数据库

同时修复了 `getInfo` 方法，确保返回 gender 和 birthday 字段。

## 📁 文件存储结构

```
petserver/
└── src/main/resources/static/images/user/avatar/
    ├── user_1_1640000000000.jpg
    ├── user_2_1640000000001.png
    └── user_3_1640000000002.webp
```

## 🔒 安全特性

1. **文件类型验证** - 只允许图片格式
2. **文件大小限制** - 最大 2MB
3. **JWT 认证** - 需要登录才能上传
4. **用户隔离** - 只能上传自己的头像
5. **旧文件清理** - 自动删除用户的旧头像

## 🔄 完整流程

1. 前端选择图片文件
2. 调用 `/api/user/upload_avatar` 接口
3. 后端验证文件类型和大小
4. 生成唯一文件名
5. 删除用户旧头像
6. 保存新文件到 `static/images/user/avatar/`
7. 更新数据库 avatar 字段
8. 返回头像 URL
9. 前端更新显示

## 📝 数据库更新

更新 `user` 表的 `avatar` 字段：
```sql
UPDATE user SET avatar = '/images/user/avatar/user_1_1640000000000.jpg' WHERE id = 1;
```

## 🌐 访问 URL

上传后的头像可通过以下 URL 访问：
```
http://localhost:3000/images/user/avatar/user_1_1640000000000.jpg
```

## ⚠️ 注意事项

1. **静态资源配置** - application.yml 已配置静态资源映射
2. **目录权限** - 确保应用有权限创建和删除文件
3. **文件名冲突** - 使用时间戳避免文件名冲突
4. **旧文件清理** - 上传新头像时自动删除旧头像
5. **错误处理** - 完善的异常捕获和错误提示

## 🧪 测试建议

### 1. 正常上传测试
```bash
curl -X POST http://localhost:3000/api/user/upload_avatar \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@avatar.jpg"
```

### 2. 文件类型测试
- 上传 JPG、PNG、GIF、WEBP - 应该成功
- 上传 PDF、TXT - 应该失败

### 3. 文件大小测试
- 上传 1MB 文件 - 应该成功
- 上传 3MB 文件 - 应该失败

### 4. 认证测试
- 不带 Token - 应该返回 401
- Token 过期 - 应该返回 401

## 📚 相关文件

- `UserController.java` - 控制器，处理上传请求
- `UserService.java` - 服务接口
- `UserServiceImpl.java` - 服务实现
- `application.yml` - 静态资源配置
- `api-upload-avatar.md` - API 文档

## 🎉 功能完成

✅ 后端文件上传接口已完成  
✅ 文件保存到 static/images/user/avatar/  
✅ 以用户 ID 命名（user_{userId}_{timestamp}）  
✅ 返回上传结果  
✅ 更新数据库 avatar 字段  
✅ 自动删除旧头像  
✅ 完善的错误处理  

现在可以启动后端服务并测试头像上传功能了！
