# Store 使用指南 - Vue3 Composition API 风格

## 📦 已更新的文件

### 1. `src/store/index.js` - Vuex + Composition API 风格
已将原有的 Vuex store 改为 Vue3 风格，并提供了 `useUserStore` hook。

### 2. `src/stores/` - Pinia 方案（可选）
如果想使用 Pinia，已准备好了完整的实现文件。

---

## 🚀 使用方式

### 方式一：在组件中使用 Vuex（传统方式）

```vue
<script setup>
import { useStore } from 'vuex';
import { computed } from 'vue';

const store = useStore();

// 获取状态
const token = computed(() => store.state.token);
const userInfo = computed(() => store.state.userInfo);
const isLoggedIn = computed(() => store.getters.isLoggedIn);

// 调用 actions
const handleLogin = async () => {
  const res = await login({ username, password });
  store.dispatch('login', {
    token: res.data.token,
    userInfo: res.data.userInfo
  });
};

const handleLogout = () => {
  store.dispatch('logout');
};
</script>
```

### 方式二：使用 useUserStore Hook（推荐）

```vue
<script setup>
import { useUserStore } from '@/store';

const userStore = useUserStore();

// 直接使用响应式数据
console.log(userStore.token.value);
console.log(userStore.userInfo.value);
console.log(userStore.isLoggedIn.value);

// 调用方法
const handleLogin = async () => {
  const res = await login({ username, password });
  userStore.login({
    token: res.data.token,
    userInfo: res.data.userInfo
  });
};

const handleLogout = () => {
  userStore.logout();
};
</script>
```

---

## 🎯 如果要切换到 Pinia（推荐）

### 1. 安装 Pinia
```bash
npm install pinia
# 或
yarn add pinia
```

### 2. 更新 `main.js`
```javascript
import { createApp } from "vue";
import App from "@/App.vue";
import router from "@/router";
import pinia from "@/stores"; // 改为从 stores 导入
import "@/assets/css/global.css";
// ... 其他导入

const app = createApp(App)
app.use(pinia) // 使用 pinia 替代 store
   .use(router)
   // ... 其他插件
   .mount("#app");
```

### 3. 在组件中使用 Pinia
```vue
<script setup>
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

// 直接访问状态（自动响应式）
console.log(userStore.token);
console.log(userStore.userInfo);
console.log(userStore.isLoggedIn);

// 调用方法
const handleLogin = async () => {
  const res = await login({ username, password });
  userStore.login({
    token: res.data.token,
    userInfo: res.data.userInfo
  });
};

const handleLogout = () => {
  userStore.logout();
};
</script>
```

---

## 📊 两种方案对比

| 特性 | Vuex | Pinia |
|------|------|-------|
| Vue3 支持 | ✅ 支持 | ✅ 原生支持 |
| TypeScript | ⚠️ 需要额外配置 | ✅ 完美支持 |
| 代码量 | 较多（mutations + actions） | 更少（直接修改 state） |
| DevTools | ✅ 支持 | ✅ 支持 |
| 学习曲线 | 中等 | 简单 |
| 官方推荐 | Vue2 时代 | Vue3 官方推荐 |

---

## 🔄 主要改进

### 1. 使用箭头函数定义 state
```javascript
// 旧写法
state: { token: '' }

// 新写法（Vue3 推荐）
state: () => ({ token: '' })
```

### 2. 提供 Composition API Hook
```javascript
export const useUserStore = () => {
  // 返回响应式的 computed 值和方法
  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    logout,
    updateUserInfo,
  };
};
```

### 3. 更好的代码组织
- 清晰的注释
- 类型提示友好
- 易于测试

---

## 💡 建议

1. **当前项目**：可以继续使用更新后的 Vuex，已经是 Vue3 风格了
2. **新项目**：强烈推荐使用 Pinia
3. **迁移**：如果要迁移到 Pinia，只需要：
   - 安装 pinia
   - 修改 main.js 中的导入
   - 组件中的使用方式几乎一样

---

## 📝 完整示例

### 登录组件示例
```vue
<template>
  <div class="login-page">
    <van-form @submit="handleLogin">
      <van-field v-model="username" label="用户名" />
      <van-field v-model="password" type="password" label="密码" />
      <van-button type="primary" native-type="submit">登录</van-button>
    </van-form>
    <div v-if="isLoggedIn">
      欢迎，{{ userInfo?.nickname }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { login } from '@/api/user';
import { showToast } from 'vant';

const store = useStore();
const router = useRouter();

const username = ref('');
const password = ref('');

// 获取登录状态
const isLoggedIn = computed(() => store.getters.isLoggedIn);
const userInfo = computed(() => store.state.userInfo);

// 登录处理
const handleLogin = async () => {
  try {
    const res = await login({
      username: username.value,
      password: password.value
    });
    
    if (res.code === 200) {
      // 保存登录信息
      store.dispatch('login', {
        token: res.data.token,
        userInfo: res.data.userInfo
      });
      
      showToast({ message: '登录成功', type: 'success' });
      
      // 跳转到目标页面或首页
      const redirect = router.currentRoute.value.query.redirect || '/home';
      router.push(redirect);
    }
  } catch (error) {
    console.error('登录失败:', error);
  }
};
</script>
```

---

## ✅ 总结

已将 `store/index.js` 更新为 Vue3 Composition API 风格：
- ✅ 使用箭头函数定义 state
- ✅ 添加详细的注释
- ✅ 提供 `useUserStore` hook
- ✅ 保持向后兼容
- ✅ 准备好 Pinia 迁移方案

现在可以在组件中使用更现代的 Composition API 风格来管理状态了！🎉
