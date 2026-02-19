# 获取用户信息失败问题排查指南

## 问题现象
个人资料页面加载时，调用 `/api/user/info` 接口失败，提示"获取用户信息失败"。

## 可能原因及排查步骤

### 1. Token 未正确传递

**检查点：**
- 浏览器 localStorage 中是否有 token
- 请求头中是否包含 `Authorization: Bearer <token>`

**排查方法：**
```javascript
// 在浏览器控制台执行
console.log('Token:', localStorage.getItem('token'));
```

**解决方案：**
- 确保登录成功后 token 已保存到 localStorage
- 检查 `request.js` 拦截器是否正确添加 Authorization 头

---

### 2. JWT Token 已过期

**检查点：**
- Token 是否已过期（默认有效期需查看后端配置）
- 后端返回 401 状态码

**排查方法：**
打开浏览器开发者工具 → Network → 查看 `/api/user/info` 请求：
- Status: 401
- Response: `{"code":401,"msg":"认证令牌无效或已过期","data":null}`

**解决方案：**
```javascript
// 重新登录获取新 token
localStorage.removeItem('token');
localStorage.removeItem('userInfo');
// 跳转到登录页
```

---

### 3. 后端服务未启动

**检查点：**
- 后端服务是否正常运行
- 端口是否正确（默认应该是 8080）

**排查方法：**
```bash
# 检查后端是否运行
curl http://localhost:8080/api/user/login
```

**解决方案：**
启动后端服务（Spring Boot 项目）

---

### 4. 跨域问题

**检查点：**
- 浏览器控制台是否有 CORS 错误
- 后端 WebConfig 是否正确配置

**排查方法：**
查看浏览器控制台错误信息：
```
Access to XMLHttpRequest at 'http://localhost:8080/api/user/info' 
from origin 'http://localhost:8081' has been blocked by CORS policy
```

**解决方案：**
后端已配置 CORS（WebConfig.java），应该不会有此问题。

---

### 5. 数据库连接问题

**检查点：**
- 数据库是否正常运行
- 用户数据是否存在

**排查方法：**
查看后端控制台日志，是否有数据库连接错误。

**解决方案：**
- 启动数据库服务
- 检查数据库配置（application.properties）

---

### 6. userId 未正确提取

**检查点：**
- JWT 拦截器是否正确提取 userId
- Controller 是否正确接收 userId

**排查方法：**
在 `JwtInterceptor.java` 的 `preHandle` 方法中添加日志：
```java
Long userId = jwtUtil.extractUserId(token);
System.out.println("提取的 userId: " + userId);
request.setAttribute("userId", userId);
```

在 `UserController.java` 的 `getInfo` 方法中添加日志：
```java
@GetMapping("/info")
public Result<UserInfo> getInfo(@RequestAttribute("userId") Long userId) {
    System.out.println("接收到的 userId: " + userId);
    // ...
}
```

---

## 快速排查步骤

### 步骤 1：检查前端
打开浏览器控制台（F12），执行：
```javascript
// 检查 token
console.log('Token:', localStorage.getItem('token'));

// 检查用户信息
console.log('UserInfo:', localStorage.getItem('userInfo'));
```

### 步骤 2：检查网络请求
1. 打开 Network 标签
2. 刷新个人资料页面
3. 找到 `/api/user/info` 请求
4. 查看：
   - Request Headers 中是否有 `Authorization: Bearer xxx`
   - Response 状态码和返回内容

### 步骤 3：检查后端日志
查看后端控制台输出，是否有异常信息。

### 步骤 4：测试接口
使用 Postman 或 curl 测试接口：
```bash
curl -X GET http://localhost:8080/api/user/info \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 常见错误及解决方案

### 错误 1：401 Unauthorized
**原因：** Token 无效或已过期  
**解决：** 重新登录

### 错误 2：500 Internal Server Error
**原因：** 后端代码异常  
**解决：** 查看后端日志，修复代码问题

### 错误 3：404 Not Found
**原因：** 接口路径错误  
**解决：** 检查前端 API 路径是否为 `/user/info`（会自动加上 `/api` 前缀）

### 错误 4：Network Error
**原因：** 后端服务未启动或网络问题  
**解决：** 启动后端服务

---

## 调试技巧

### 1. 添加请求日志
在 `ProfileView.vue` 中添加详细日志：
```javascript
const loadUserInfo = async () => {
  try {
    console.log('开始加载用户信息...');
    console.log('Token:', localStorage.getItem('token'));
    
    const response = await getCurrentUserInfo();
    console.log('API 响应:', response);
    
    if (response.code === 200 && response.data) {
      console.log('用户信息:', response.data);
      userStore.setUserInfo(response.data);
    }
  } catch (error) {
    console.error('加载失败:', error);
    console.error('错误详情:', error.response);
  }
};
```

### 2. 使用浏览器断点调试
在 `request.js` 的响应拦截器中设置断点，查看实际返回的数据。

### 3. 检查后端返回格式
确保后端返回格式为：
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "id": 1,
    "username": "test",
    "nickname": "测试用户",
    "gender": "男",
    "birthday": "2000-01-01",
    "email": "test@example.com",
    "avatar": "http://...",
    "status": "active",
    "role": "user"
  }
}
```

---

## 预防措施

1. **Token 刷新机制**：实现 token 自动刷新，避免过期
2. **错误提示优化**：根据不同错误码显示不同提示
3. **离线缓存**：缓存用户信息，减少接口调用
4. **重试机制**：网络失败时自动重试

---

## 联系支持

如果以上方法都无法解决问题，请提供：
1. 浏览器控制台完整错误信息
2. Network 标签中的请求详情（Request/Response）
3. 后端控制台日志
