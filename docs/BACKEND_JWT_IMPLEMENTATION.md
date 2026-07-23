# 后端JWT拦截器实现文档

## 📦 已实现的功能

### 一、Spring Boot 后端（Java）

#### 1. JWT拦截器 (`JwtInterceptor.java`)
- ✅ 验证请求头中的 `Authorization: Bearer {token}`
- ✅ 自动提取用户ID并存储到请求属性中
- ✅ 401错误统一返回JSON格式
- ✅ 支持OPTIONS预检请求

#### 2. Web配置 (`WebConfig.java`)
- ✅ 注册JWT拦截器
- ✅ 配置拦截路径和排除路径
- ✅ 配置CORS跨域支持
- ✅ 排除的路径：
  - `/user/login` - 登录接口
  - `/user/register` - 注册接口
  - `/swipe/**` - 轮播图（公开）
  - `/recommend/**` - 推荐商品（公开）
  - Swagger相关路径

#### 3. JWT工具类 (`JwtUtil.java`)
- ✅ 生成JWT Token
- ✅ 验证Token有效性
- ✅ 提取用户ID
- ✅ 检查Token是否过期

#### 4. 用户控制器 (`UserController.java`)
- ✅ `POST /api/user/login` - 登录接口（返回token）
- ✅ `POST /api/user/register` - 注册接口
- ✅ `GET /api/user/info` - 获取当前用户信息（需要JWT认证）
- ✅ `POST /api/user/logout` - 退出登录（需要JWT认证）

#### 5. 用户服务 (`UserServiceImpl.java`)
- ✅ 用户注册逻辑
- ✅ 用户登录逻辑（密码验证 + Token生成）
- ✅ 获取用户信息（不返回密码）

---

### 二、Node.js/Express 后端

#### 1. JWT中间件 (`jwtMiddleware.js`)
- ✅ `jwtMiddleware` - 强制JWT认证中间件
- ✅ `optionalJwtMiddleware` - 可选JWT认证中间件
- ✅ 验证Token并提取用户信息
- ✅ 401错误统一返回JSON格式

#### 2. JWT工具类 (`jwtUtil.js`)
- ✅ 生成JWT Token
- ✅ 验证Token
- ✅ 解码Token
- ✅ 从请求头提取Token

#### 3. 用户路由 (`routes/user.js`)
- ✅ `POST /api/user/register` - 注册接口
- ✅ `POST /api/user/login` - 登录接口（返回token）
- ✅ `GET /api/user/info` - 获取当前用户信息（需要JWT认证）
- ✅ `POST /api/user/logout` - 退出登录（需要JWT认证）

#### 4. 应用配置 (`app.js`)
- ✅ 注册用户路由

---

## 🚀 使用方式

### Spring Boot 后端

#### 1. 启动项目
```bash
cd petserver
mvn spring-boot:run
```

#### 2. 测试接口

**登录接口：**
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
```

**响应：**
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**获取用户信息（需要Token）：**
```bash
curl -X GET http://localhost:8080/api/user/info \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**响应：**
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": {
    "id": 1,
    "username": "test",
    "email": "test@example.com",
    "nickname": "测试用户",
    "avatar": null,
    "status": "active",
    "role": "user"
  }
}
```

#### 3. 在Controller中获取当前用户ID
```java
@GetMapping("/profile")
public Result<User> getProfile(@RequestAttribute("userId") Long userId) {
    // userId 已经从JWT中提取出来
    User user = userService.getUserById(userId);
    return Result.success(user);
}
```

---

### Node.js 后端

#### 1. 安装依赖
```bash
cd vue-pet/server
npm install
```

#### 2. 启动项目
```bash
npm start
```

#### 3. 测试接口

**注册接口：**
```bash
curl -X POST http://localhost:3000/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456","email":"test@example.com"}'
```

**登录接口：**
```bash
curl -X POST http://localhost:3000/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
```

**响应：**
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "test",
      "email": "test@example.com",
      "nickname": "test",
      "avatar": null,
      "role": "user"
    }
  }
}
```

**获取用户信息（需要Token）：**
```bash
curl -X GET http://localhost:3000/api/user/info \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 4. 在路由中使用JWT中间件
```javascript
const { jwtMiddleware } = require('../middlewares/jwtMiddleware');

// 需要认证的路由
router.get("/protected", jwtMiddleware, (req, res) => {
  // req.userId 和 req.username 已经从JWT中提取出来
  res.json({
    code: 200,
    msg: '访问成功',
    data: {
      userId: req.userId,
      username: req.username
    }
  });
});
```

---

## 📝 配置说明

### JWT密钥配置

**Spring Boot：**
在 `JwtUtil.java` 中修改：
```java
private String secret = "your-secret-key-here";
```

**Node.js：**
设置环境变量或在 `jwtUtil.js` 中修改：
```javascript
this.secret = process.env.JWT_SECRET || 'your-secret-key-here';
```

### Token过期时间

**Spring Boot：**
```java
private long expirationTime = 1000 * 60 * 60 * 24 * 7; // 7天
```

**Node.js：**
```javascript
this.expirationTime = '7d'; // 7天
```

---

## 🔒 安全建议

1. **生产环境必须修改JWT密钥**，使用强随机字符串
2. **使用HTTPS**传输Token
3. **Token过期时间**不宜过长
4. **敏感操作**建议二次验证
5. **密码加密**使用BCrypt
6. **防止XSS攻击**，不要将Token存储在localStorage（可以考虑httpOnly Cookie）

---

## 📂 文件结构

### Spring Boot
```
petserver/src/main/java/com/yellow/petshop/
├── config/
│   └── WebConfig.java              # Web配置（拦截器、CORS）
├── interceptor/
│   └── JwtInterceptor.java         # JWT拦截器
├── util/
│   └── JwtUtil.java                # JWT工具类
├── controller/
│   └── UserController.java         # 用户控制器
├── service/
│   ├── UserService.java            # 用户服务接口
│   └── Impl/
│       └── UserServiceImpl.java    # 用户服务实现
└── model/
    └── user/
        ├── User.java               # 用户实体
        ├── UserInfo.java           # 用户信息DTO
        ├── LoginDTO.java           # 登录DTO
        └── RegisterDTO.java        # 注册DTO
```

### Node.js
```
vue-pet/server/
├── middlewares/
│   └── jwtMiddleware.js            # JWT中间件
├── utils/
│   └── jwtUtil.js                  # JWT工具类
├── routes/
│   └── user.js                     # 用户路由
└── app.js                          # 应用入口
```

---

## ✅ 完成清单

### Spring Boot 后端
- ✅ JWT拦截器实现
- ✅ Web配置（拦截器注册、CORS）
- ✅ JWT工具类
- ✅ 用户登录/注册接口
- ✅ 获取用户信息接口（需认证）
- ✅ 退出登录接口（需认证）

### Node.js 后端
- ✅ JWT中间件实现
- ✅ JWT工具类
- ✅ 用户登录/注册接口
- ✅ 获取用户信息接口（需认证）
- ✅ 退出登录接口（需认证）
- ⚠️ 需要安装依赖：`npm install jsonwebtoken bcryptjs`

---

## 🎉 总结

两个后端的JWT拦截器已全部实现完成！

- **Spring Boot**：使用HandlerInterceptor实现，配置灵活
- **Node.js**：使用Express中间件实现，简洁高效

前后端已完全打通JWT认证流程，可以直接使用！🚀
