# JWT认证优化说明文档

## 📋 优化概述

本次优化实现了商家端和客户端登录业务的完全分离，通过JWT token中的 `userType` 字段区分用户类型，并使用专门的拦截器防止越权操作。

---

## 🔧 核心改动

### 1. JWT Token 结构优化

**JwtUtil.java** - 在token中增加 `userType` 字段：

```java
// 客户端token包含: userType = "customer"
public String generateToken(User user) {
    .claim("userType", "customer")
}

// 商家端token包含: userType = "merchant"  
public static String generateToken(Long userId, String username) {
    .claim("userType", "merchant")
}
```

**新增工具方法：**
- `extractUserType(token)` - 提取用户类型
- `getUserTypeFromToken(token)` - 静态方法获取用户类型
- `isMerchantToken(token)` - 验证是否为商家端token
- `isCustomerToken(token)` - 验证是否为客户端token

---

### 2. 创建专用拦截器

#### **MerchantJwtInterceptor.java** - 商家端拦截器

**功能：**
- 验证token有效性
- 验证token类型必须为 `merchant`
- 提取商家ID存入request属性
- 防止客户端token访问商家端接口

**拦截路径：** `/api/merchant/**`

**排除路径：** `/api/merchant/login`, `/api/merchant/register`

**错误响应：**
- 401: 未提供token / token无效或过期 / token解析失败
- 403: 无权访问商家端接口，请使用商家账号登录

---

#### **CustomerJwtInterceptor.java** - 客户端拦截器

**功能：**
- 验证token有效性
- 验证token类型必须为 `customer`
- 提取用户ID存入request属性
- 防止商家端token访问客户端接口

**拦截路径：**
- `/api/user/info`, `/api/user/update_info`, `/api/user/upload_avatar`
- `/api/address/**` - 地址管理
- `/api/cart/**` - 购物车
- `/api/order/**` - 订单管理
- `/api/favorite/**` - 收藏
- `/api/comment/**` - 评论
- `/api/browse/**` - 浏览历史
- `/api/pet/**` - 宠物档案
- `/api/payment/**` - 支付相关

**排除路径：** `/api/user/login`, `/api/user/register`

**错误响应：**
- 401: 未提供token / token无效或过期 / token解析失败
- 403: 无权访问客户端接口，请使用客户账号登录

---

### 3. WebConfig 配置优化

**WebConfig.java** - 注册两个独立的拦截器：

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    // 商家端拦截器 - 优先级1
    registry.addInterceptor(merchantJwtInterceptor)
            .addPathPatterns("/api/merchant/**")
            .excludePathPatterns("/api/merchant/login", "/api/merchant/register")
            .order(1);
    
    // 客户端拦截器 - 优先级2
    registry.addInterceptor(customerJwtInterceptor)
            .addPathPatterns("/api/user/info", "/api/address/**", ...)
            .excludePathPatterns("/api/user/login", "/api/user/register")
            .order(2);
}
```

**公共接口（无需登录）：**
- `/api/goods/**` - 商品浏览
- `/api/category/**` - 分类浏览
- `/api/swipe/**` - 轮播图
- `/api/recommend/**` - 推荐
- `/api/search/**` - 搜索

---

## 🔒 安全机制

### 防止越权操作

| 场景 | 行为 | 结果 |
|------|------|------|
| 客户端token访问商家端接口 | MerchantJwtInterceptor拦截 | 403 - 无权访问商家端接口 |
| 商家端token访问客户端接口 | CustomerJwtInterceptor拦截 | 403 - 无权访问客户端接口 |
| 无token访问需登录接口 | 拦截器拦截 | 401 - 未提供认证令牌 |
| 过期token访问 | 拦截器拦截 | 401 - 认证令牌无效或已过期 |
| 伪造token访问 | 拦截器拦截 | 401 - 认证令牌解析失败 |

---

## 📊 登录业务流程

### 商家端登录流程

```
1. POST /api/merchant/login
   ↓
2. MerchantController.login()
   ↓
3. MerchantService.login()
   - 查询商家信息
   - 验证状态（status=0则禁用）
   - BCrypt验证密码
   - 更新最后登录时间
   - 记录登录日志
   - 生成token (userType="merchant")
   ↓
4. 返回token
   ↓
5. 后续请求携带token访问 /api/merchant/**
   ↓
6. MerchantJwtInterceptor拦截验证
   - 验证token有效性
   - 验证userType="merchant"
   - 提取merchantId存入request
   ↓
7. Controller处理业务
```

### 客户端登录流程

```
1. POST /api/user/login
   ↓
2. UserController.login()
   ↓
3. UserService.login()
   - 查询用户信息
   - 验证状态（status="active"）
   - BCrypt验证密码
   - 生成token (userType="customer")
   ↓
4. 返回token
   ↓
5. 后续请求携带token访问需登录接口
   ↓
6. CustomerJwtInterceptor拦截验证
   - 验证token有效性
   - 验证userType="customer"
   - 提取userId存入request
   ↓
7. Controller处理业务
```

---

## 🎯 使用示例

### 前端请求示例

**商家端登录：**
```javascript
// 登录
const response = await axios.post('/api/merchant/login', {
  username: 'merchant001',
  password: '123456'
});
const token = response.data.data;

// 访问商家端接口
axios.get('/api/merchant/dashboard', {
  headers: { Authorization: `Bearer ${token}` }
});
```

**客户端登录：**
```javascript
// 登录
const response = await axios.post('/api/user/login', {
  username: 'customer001',
  password: '123456'
});
const token = response.data.data;

// 访问客户端接口
axios.get('/api/user/info', {
  headers: { Authorization: `Bearer ${token}` }
});
```

---

## ✅ 优化效果

1. **安全性提升**：完全隔离商家端和客户端权限，防止越权操作
2. **代码清晰**：职责分离，每个拦截器只负责一种用户类型
3. **易于维护**：新增接口只需在WebConfig中配置拦截路径
4. **错误明确**：不同场景返回不同的错误码和提示信息
5. **向后兼容**：保持原有接口路径和参数不变

---

## 📝 注意事项

1. **Token存储**：前端需要区分存储商家端和客户端token
2. **Token刷新**：token过期后需要重新登录
3. **CORS配置**：已配置允许携带Authorization头
4. **密码加密**：使用BCrypt加密，不可逆
5. **日志记录**：商家端登录会记录详细日志（IP、UA、状态）

---

## 🔄 后续优化建议

1. 实现token刷新机制（refresh token）
2. 添加token黑名单（用于强制登出）
3. 实现多设备登录管理
4. 添加登录失败次数限制
5. 实现验证码机制防止暴力破解
6. 添加操作日志审计功能

---

**优化完成时间：** 2026-03-03  
**涉及文件：**
- JwtUtil.java (优化)
- MerchantJwtInterceptor.java (新增)
- CustomerJwtInterceptor.java (新增)
- WebConfig.java (优化)
- JwtInterceptor.java (已删除)
