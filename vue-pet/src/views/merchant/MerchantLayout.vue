<template>
  <div class="merchant-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo">
          <svg class="logo-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
            <path d="M2 17l10 5 10-5M2 12l10 5 10-5"></path>
          </svg>
          <span class="logo-text">商家管理</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
        >
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path :d="item.icon"></path>
          </svg>
          <span class="nav-text">{{ item.name }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <button @click="handleLogout" class="logout-btn">
          <svg class="logout-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
            <polyline points="16 17 21 12 16 7"></polyline>
            <line x1="21" y1="12" x2="9" y2="12"></line>
          </svg>
          退出登录
        </button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 顶部栏 -->
      <header class="top-bar">
        <div class="top-bar-left">
          <h1 class="page-title">{{ currentPageTitle }}</h1>
        </div>
        <div class="top-bar-right">
          <div class="user-info">
            <div class="user-avatar">
              {{ merchantInfo.username?.charAt(0).toUpperCase() || 'A' }}
            </div>
            <span class="user-name">{{ merchantInfo.username || '商家' }}</span>
          </div>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="page-content">
        <router-view></router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useMerchantStore } from '@/stores/merchant';
import { getMerchantInfo } from '@/api/merchant';
import { showConfirmDialog, showSuccessToast } from 'vant';

const router = useRouter();
const route = useRoute();
const merchantStore = useMerchantStore();

const merchantInfo = ref({});

const menuItems = [
  {
    name: '数据概览',
    path: '/merchant/dashboard',
    icon: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10'
  },
  {
    name: '商品管理',
    path: '/merchant/goods',
    icon: 'M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z M3.27 6.96L12 12.01l8.73-5.05 M12 22.08V12'
  },
  {
    name: '会员管理',
    path: '/merchant/member',
    icon: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2 M23 21v-2a4 4 0 0 0-3-3.87 M16 3.13a4 4 0 0 1 0 7.75 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z'
  },
  {
    name: '订单管理',
    path: '/merchant/order',
    icon: 'M9 11H3v10h6V11z M21 11h-6v10h6V11z M15 3H9v6h6V3z'
  }
];

const currentPageTitle = computed(() => {
  const item = menuItems.find(item => route.path.startsWith(item.path));
  return item ? item.name : '商家管理';
});

const isActive = (path) => {
  return route.path.startsWith(path);
};

const handleLogout = () => {
  showConfirmDialog({
    title: '退出登录',
    message: '确定要退出登录吗？',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#98D8C8'
  }).then(() => {
    merchantStore.logout();
    showSuccessToast('已退出登录');
    router.push('/merchant/login');
  }).catch(() => {
    // 取消操作
  });
};

onMounted(async () => {
  try {
    const response = await getMerchantInfo();
    if (response && response.code === 200) {
      merchantInfo.value = response.data;
      merchantStore.updateMerchantInfo(response.data);
    }
  } catch (error) {
    console.error('获取商家信息失败:', error);
  }
});
</script>

<style scoped>
.merchant-layout {
  display: flex;
  height: 100vh;
  background: #f0f2f5;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 侧边栏 */
.sidebar {
  width: 260px;
  background: white;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.03);
}

.sidebar-header {
  padding: 28px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  stroke-width: 2;
  color: #98D8C8;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #2d3436;
  letter-spacing: 0.5px;
}

.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  margin-bottom: 6px;
  border-radius: 10px;
  color: #636e72;
  text-decoration: none;
  transition: all 0.2s ease;
  position: relative;
}

.nav-item:hover {
  background: #f8f9fa;
  color: #2d3436;
}

.nav-item.active {
  background: linear-gradient(135deg, rgba(152, 216, 200, 0.15) 0%, rgba(107, 207, 159, 0.15) 100%);
  color: #98D8C8;
  font-weight: 600;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(135deg, #98D8C8 0%, #6BCF9F 100%);
  border-radius: 0 4px 4px 0;
}

.nav-icon {
  width: 22px;
  height: 22px;
  stroke-width: 2;
}

.nav-text {
  font-size: 15px;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  color: #636e72;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.logout-btn:hover {
  background: #fff5f5;
  border-color: #ff6b6b;
  color: #ff6b6b;
}

.logout-icon {
  width: 18px;
  height: 18px;
  stroke-width: 2;
}

/* 主内容区 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.top-bar {
  height: 70px;
  background: white;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #2d3436;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #98D8C8 0%, #6BCF9F 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
}

.user-name {
  font-size: 15px;
  color: #2d3436;
  font-weight: 500;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}
</style>
