# 迁移到 Pinia 完成文档

## 📋 完成的修改

### 1. 安装 Pinia
在 `package.json` 中添加了 Pinia 依赖：
```json
"pinia": "^2.1.7"
```

**安装命令：**
```bash
npm install pinia
```

### 2. 创建 Pinia Stores

#### `src/stores/index.js`
创建 Pinia 实例：
```javascript
import { createPinia } from 'pinia';

const pinia = createPinia();

export default pinia;
```

#### `src/stores/user.js`
使用 Composition API 风格创建用户 store：
```javascript
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(localStorage.getItem('token') || '');
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'));

  // Getters
  const getToken = computed(() => token.value);
  const getUserInfo = computed(() => userInfo.value);
  const isLoggedIn = computed(() => !!token.value);

  // Actions
  function login({ token, userInfo }) { ... }
  function logout() { ... }
  function updateUserInfo(userInfo) { ... }

  return { token, userInfo, getToken, getUserInfo, isLoggedIn, login, logout, updateUserInfo };
});
```

### 3. 更新 main.js
```javascript
// 旧代码
import store from "@/store";
app.use(store)

// 新代码
import pinia from "@/stores";
app.use(pinia)
```

### 4. 更新组件使用方式

#### Login.vue
```javascript
// 旧代码 (Vuex)
import { useStore } from 'vuex';
const store = useStore();
store.dispatch('login', { token, userInfo });

// 新代码 (Pinia)
import { useUserStore } from '@/stores/user';
const userStore = useUserStore();
userStore.login({ token, userInfo });
```

## 🎯 Pinia vs Vuex 对比

### Vuex 写法
```javascript
import { useStore } from 'vuex';

const store = useStore();

// 获取状态
const token = computed(() => store.state.token);
const isLoggedIn = computed(() => store.getters.isLoggedIn);

// 调用 actions
store.dispatch('login', { token, userInfo });
store.dispatch('logout');
```

### Pinia 写法（更简洁）
```javascript
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

// 直接访问状态（自动响应式）
const token = userStore.token;
const isLoggedIn = userStore.isLoggedIn;

// 直接调用方法
userStore.login({ token, userInfo });
userStore.logout();
```

## ✅ Pinia 的优势

1. **更简洁的 API**
   - 不需要 mutations
   - 直接修改 state
   - 更少的样板代码

2. **完美的 TypeScript 支持**
   - 自动类型推导
   - 更好的 IDE 提示

3. **Composition API 风格**
   - 使用 `ref` 和 `computed`
   - 更符合 Vue 3 的设计理念

4. **更小的包体积**
   - 约 1KB (gzipped)
   - 比 Vuex 更轻量

5. **模块化**
   - 每个 store 都是独立的
   - 不需要嵌套的 modules

## 🚀 使用示例

### 在组件中使用
```vue
<script setup>
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

// 访问状态
console.log(userStore.token);
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
  router.push('/login');
};
</script>
```

### 在 Options API 中使用
```javascript
import { mapStores } from 'pinia';
import { useUserStore } from '@/stores/user';

export default {
  computed: {
    ...mapStores(useUserStore),
    token() {
      return this.userStore.token;
    }
  },
  methods: {
    handleLogin() {
      this.userStore.login({ token, userInfo });
    }
  }
}
```

## 📝 迁移清单

- ✅ 安装 Pinia
- ✅ 创建 `stores/index.js`
- ✅ 创建 `stores/user.js`
- ✅ 更新 `main.js`
- ✅ 更新 `Login.vue`
- ⚠️ 保留 `store/index.js`（向后兼容）

## 🔄 下一步

如果需要完全移除 Vuex：
1. 删除 `src/store` 文件夹
2. 从 `package.json` 中移除 `vuex` 依赖
3. 运行 `npm uninstall vuex`

## 🎉 总结

Pinia 已成功集成！现在可以使用更现代、更简洁的状态管理方案了。
