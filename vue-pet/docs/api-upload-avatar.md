# 头像上传接口文档

## 接口信息

**接口名称：** 上传用户头像  
**接口路径：** `/api/user/upload_avatar`  
**请求方法：** `POST`  
**需要认证：** 是（需要 JWT Token）  
**Content-Type：** `multipart/form-data`

---

## 请求参数

### Headers

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| Authorization | String | 是 | JWT Token，格式：`Bearer <token>` |
| Content-Type | String | 是 | `multipart/form-data` |

### Body (FormData)

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 头像图片文件 |

### 文件限制

- **文件类型：** image/jpeg, image/png, image/gif, image/webp
- **文件大小：** 最大 2MB
- **图片尺寸：** 建议 200x200 ~ 1000x1000 像素

---

## 响应参数

### 成功响应 (200)

```json
{
  "code": 200,
  "msg": "上传成功",
  "data": {
    "avatarUrl": "https://example.com/uploads/avatars/user_123_1234567890.jpg",
    "fileName": "user_123_1234567890.jpg",
    "fileSize": 102400,
    "uploadTime": "2024-01-20T10:30:00Z"
  }
}
```

### 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200 表示成功 |
| msg | String | 响应消息 |
| data | Object | 响应数据 |
| data.avatarUrl | String | 头像完整 URL 地址 |
| data.fileName | String | 保存的文件名 |
| data.fileSize | Integer | 文件大小（字节） |
| data.uploadTime | String | 上传时间（ISO 8601 格式） |

### 错误响应

#### 1. 未提供文件 (400)

```json
{
  "code": 400,
  "msg": "请选择要上传的文件",
  "data": null
}
```

#### 2. 文件类型不支持 (400)

```json
{
  "code": 400,
  "msg": "不支持的文件类型，仅支持 jpg、png、gif、webp 格式",
  "data": null
}
```

#### 3. 文件大小超限 (400)

```json
{
  "code": 400,
  "msg": "文件大小超过限制，最大支持 2MB",
  "data": null
}
```

#### 4. 未认证 (401)

```json
{
  "code": 401,
  "msg": "未提供认证令牌",
  "data": null
}
```

#### 5. Token 无效 (401)

```json
{
  "code": 401,
  "msg": "认证令牌无效或已过期",
  "data": null
}
```

#### 6. 服务器错误 (500)

```json
{
  "code": 500,
  "msg": "文件上传失败，请稍后重试",
  "data": null
}
```

---

## 请求示例

### cURL

```bash
curl -X POST http://localhost:8080/api/user/upload_avatar \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -F "file=@/path/to/avatar.jpg"
```

### JavaScript (Axios)

```javascript
import axios from 'axios';

const uploadAvatar = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  try {
    const response = await axios.post('/api/user/upload_avatar', formData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'multipart/form-data'
      }
    });
    
    console.log('上传成功:', response.data);
    return response.data;
  } catch (error) {
    console.error('上传失败:', error);
    throw error;
  }
};
```

### Vue 3 (使用 Vant Uploader)

```vue
<template>
  <van-uploader
    :after-read="handleAvatarUpload"
    :max-size="2 * 1024 * 1024"
    @oversize="onOversize"
    accept="image/*"
  >
    <van-button type="primary">上传头像</van-button>
  </van-uploader>
</template>

<script setup>
import { uploadAvatar } from '@/api/user';
import { showLoadingToast, closeToast, showSuccessToast, showFailToast } from 'vant';

const handleAvatarUpload = async (file) => {
  try {
    showLoadingToast({ message: '上传中...', forbidClick: true, duration: 0 });
    
    const formData = new FormData();
    formData.append('file', file.file);
    
    const response = await uploadAvatar(formData);
    closeToast();
    
    if (response.code === 200) {
      showSuccessToast('上传成功');
      console.log('头像URL:', response.data.avatarUrl);
    } else {
      showFailToast(response.msg);
    }
  } catch (error) {
    closeToast();
    showFailToast('上传失败');
  }
};

const onOversize = () => {
  showFailToast('图片大小不能超过 2MB');
};
</script>
```

---

## 后端实现要点

### 1. 文件验证

```java
// 验证文件类型
private static final List<String> ALLOWED_TYPES = Arrays.asList(
    "image/jpeg", "image/png", "image/gif", "image/webp"
);

// 验证文件大小（2MB）
private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
```

### 2. 文件命名规则

```
格式：user_{userId}_{timestamp}.{extension}
示例：user_123_1640000000000.jpg
```

### 3. 存储路径

```
本地存储：/uploads/avatars/
访问路径：http://localhost:8080/uploads/avatars/user_123_1640000000000.jpg
```

### 4. 数据库更新

上传成功后，需要更新用户表中的 `avatar` 字段：

```sql
UPDATE user SET avatar = ? WHERE id = ?
```

### 5. 旧头像处理

- 上传新头像时，删除旧头像文件（如果存在）
- 避免服务器存储空间浪费

---

## 安全建议

1. **文件类型验证**
   - 检查 MIME 类型
   - 检查文件扩展名
   - 验证文件头（魔数）

2. **文件大小限制**
   - 前端限制：2MB
   - 后端限制：2MB（双重验证）

3. **文件名处理**
   - 使用 UUID 或时间戳生成唯一文件名
   - 避免使用用户上传的原始文件名

4. **存储路径**
   - 不要将文件存储在 Web 根目录
   - 使用专门的文件服务器或 CDN

5. **访问控制**
   - 头像文件可公开访问
   - 但上传接口需要认证

---

## 测试用例

### 1. 正常上传

- **输入：** 1MB 的 JPG 图片
- **预期：** 上传成功，返回头像 URL

### 2. 文件过大

- **输入：** 3MB 的 PNG 图片
- **预期：** 返回错误 "文件大小超过限制"

### 3. 不支持的格式

- **输入：** PDF 文件
- **预期：** 返回错误 "不支持的文件类型"

### 4. 未认证

- **输入：** 不带 Token 的请求
- **预期：** 返回 401 错误

### 5. Token 过期

- **输入：** 过期的 Token
- **预期：** 返回 401 错误 "认证令牌已过期"

---

## 注意事项

1. **前端预览**
   - 上传前可以使用 `URL.createObjectURL()` 预览图片
   - 上传成功后使用服务器返回的 URL

2. **图片压缩**
   - 建议前端上传前进行图片压缩
   - 可使用 `compressorjs` 等库

3. **进度显示**
   - Vant Uploader 支持显示上传进度
   - 可通过 `before-read` 钩子实现自定义逻辑

4. **错误处理**
   - 网络错误时显示友好提示
   - 提供重试机制

5. **缓存处理**
   - 头像 URL 变化时，添加时间戳参数避免缓存
   - 例如：`avatar.jpg?t=1640000000000`

---

## 相关接口

- [获取用户信息](/api/user/info) - GET
- [更新用户信息](/api/user/update_info) - POST

---

## 更新日志

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2024-01-20 | 初始版本 |
