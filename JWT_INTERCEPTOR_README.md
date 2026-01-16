# JWT拦截器实现说明

## 功能概述
已成功实现完整的JWT拦截器系统，包括请求拦截、响应拦截、路由守卫和状态管理。

## 实现的功能

### 1. 请求拦截器 (`src/utils/request.js`)
- ✅ 自动在请求头中添加JWT Token (`Authorization: Bearer ${token}`)
- ✅ 从localStorage读取token
- ✅ 401错误自动清除token并跳转登录页
- ✅ 完善的错误处理机制

### 2. 认证工具函数 (`src/utils/auth.js`)
提供了完整的认证相关工具函数：
- `getToken()` - 获取token
- `setToken(token)` - 设置token
- `removeToken()` - 移除token
- `getUserInfo()` - 获取用户信息
- `setUserInfo(userInfo)` - 设置用户信息
- `clearAuth()` - 清除所有认证信息
- `isAuthenticated()` - 检查是否已登录
- `parseJWT(token)` - 解析JWT token
- `isTokenExpired(token)` - 检查token是否过期
- `getTokenRemainingTime(token)` - 获取token剩余有效时间

### 3. Vuex状态管理 (`src/store/index.js`)
- ✅ 管理token和用户信息状态
- ✅ 自动同步到localStorage
- ✅ 提供登录、退出、更新用户信息等actions
- ✅ 提供getters方便获取状态

### 4. 路由守卫 (`src/router/index.js`)
- ✅ 根据路由meta.requiresAuth判断是否需要登录
- ✅ 未登录自动跳转到登录页
- ✅ 保存目标路由，登录后可跳转回来
- ✅ 显示友好的提示信息

### 5. 用户API扩展 (`src/api/user.js`)
新增API接口：
- `getCurrentUserInfo()` - 获取当前登录用户信息
- `logout()` - 退出登录

## 使用示例

### 登录后保存token
```javascript
import { login } from '@/api/user';
import { useStore } from 'vuex';

const store = useStore();

// 登录
const handleLogin = async () => {
  const res = await login({ username, password });
  if (res.code === 200) {
    // 保存token和用户信息到store（会自动保存到localStorage）
    store.dispatch('login', {
      token: res.data.token,
      userInfo: res.data.userInfo
    });
    // 跳转到目标页面
    router.push('/my');
  }
};
```

### 退出登录
```javascript
import { logout } from '@/api/user';
import { useStore } from 'vuex';

const store = useStore();

const handleLogout = async () => {
  await logout();
  // 清除本地登录信息
  store.dispatch('logout');
  // 跳转到登录页
  router.push('/login');
};
```

### 检查登录状态
```javascript
import { isAuthenticated } from '@/utils/auth';
import { useStore } from 'vuex';

const store = useStore();

// 方式1：使用工具函数
if (isAuthenticated()) {
  console.log('已登录');
}

// 方式2：使用Vuex getter
if (store.getters.isLoggedIn) {
  console.log('已登录');
}
```

### 获取用户信息
```javascript
import { useStore } from 'vuex';

const store = useStore();
const userInfo = store.getters.getUserInfo;
console.log(userInfo);
```

## 路由配置
需要登录的路由设置 `meta: { requiresAuth: true }`：
```javascript
{
  path: "/my",
  name: "my",
  component: () => import("../views/MyView.vue"),
  meta: { requiresAuth: true } // 需要登录
}
```

## 注意事项
1. 后端返回的token应该在登录接口的响应中提供
2. token格式应为标准JWT格式
3. 后端需要在响应头或响应体中返回token
4. 401错误会自动清除token并跳转登录页
5. 所有需要认证的API请求会自动携带token

## 已配置需要登录的页面
- ✅ `/car` - 购物车
- ✅ `/my` - 个人中心

## 无需登录的页面
- ✅ `/home` - 首页
- ✅ `/list` - 列表页
- ✅ `/login` - 登录页
- ✅ `/newhome` - 新首页
