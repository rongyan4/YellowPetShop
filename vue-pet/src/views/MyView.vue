<template>
  <div class="my-view">
    <!-- 头部用户信息 -->
    <div class="header">
      <div class="user-info" @click="handleUserNameClick">
        <div class="user-name">
          {{ displayName }}
          <i class="arrow-right">›</i>
        </div>
        <div class="member-days" v-if="isLoggedIn">{{ memberDaysText }}</div>
      </div>
      <div class="qr-icon">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <rect x="3" y="3" width="8" height="8" fill="currentColor"/>
          <rect x="13" y="3" width="8" height="8" fill="currentColor"/>
          <rect x="3" y="13" width="8" height="8" fill="currentColor"/>
          <rect x="13" y="13" width="8" height="8" fill="currentColor"/>
        </svg>
      </div>
    </div>

    <!-- 会员贵宾卡片 - 仅登录后显示 -->
    <div class="vip-card" v-if="isLoggedIn" :style="vipCardStyle">
      <div class="vip-title">{{ vipLevelText }}</div>
      <div class="vip-progress">
        <div class="progress-text">{{ vipProgressText }}</div>
      </div>
    </div>

    <!-- 资产统计 -->
    <div class="assets-section">
      <div class="asset-item" @click="handleAssetClick('coupons')">
        <div class="asset-value">{{ displayAssetValue(assets.coupons) }}</div>
        <div class="asset-label">优惠券</div>
      </div>
      <div class="asset-item" @click="handleAssetClick('cards')">
        <div class="asset-value">{{ displayAssetValue(assets.cards) }}</div>
        <div class="asset-label">礼品卡</div>
      </div>
      <div class="asset-item" @click="handleAssetClick('wallet')">
        <div class="asset-value">{{ displayAssetValue(assets.wallet, true) }}</div>
        <div class="asset-label">钱包</div>
      </div>
      <div class="asset-item" @click="handleAssetClick('points')">
        <div class="asset-value">{{ displayAssetValue(assets.points) }}</div>
        <div class="asset-label">积分</div>
      </div>
    </div>

    <!-- 分割线 -->
    <div class="divider"></div>

    <!-- 菜单列表 -->
    <div class="menu-list">
      <div class="menu-item" @click="handleMenuClick('orders')">
        <span class="menu-text">我的订单</span>
        <i class="arrow-right">›</i>
      </div>

      <div class="menu-item" @click="handleMenuClick('browse')">
        <span class="menu-text">浏览记录</span>
        <i class="arrow-right">›</i>
      </div>

      <div class="menu-item" @click="handleMenuClick('favorites')">
        <span class="menu-text">我的收藏</span>
        <i class="arrow-right">›</i>
      </div>

      <div class="menu-item" @click="handleMenuClick('service')">
        <span class="menu-text">我的客服</span>
        <i class="arrow-right">›</i>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <LoginChoice v-if="showLoginModal" @close="closeLoginModal" />

    <!-- TabBar -->
    <TabBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';
import LoginChoice from '@/components/login/LoginChoice.vue';
import TabBar from '@/components/TabBar.vue';
import { getWalletInfoSafe } from '@/api/payment';
import { showToast } from 'vant';

const userStore = useUserStore();
const router = useRouter();

// 登录弹窗控制
const showLoginModal = ref(false);

// 登录状态
const isLoggedIn = computed(() => userStore.isLoggedIn);

// 显示的用户名
const displayName = computed(() => {
  if (isLoggedIn.value) {
    return userStore.userInfo?.nickname || userStore.userInfo?.username || '熔岩';
  }
  return '请登录';
});

// 会员加入时间展示
const memberDaysText = computed(() => {
  const info = userStore.userInfo;
  if (!info || !info.createTime) {
    return '欢迎成为大黄宠物会员';
  }
  const joinDate = new Date(info.createTime);
  if (Number.isNaN(joinDate.getTime())) {
    return '欢迎成为大黄宠物会员';
  }
  const now = new Date();
  const diffTime = now.getTime() - joinDate.getTime();
  if (diffTime <= 0) {
    return '成为会员第1天';
  }
  const totalDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
  const years = Math.floor(totalDays / 365);
  const days = totalDays % 365;
  if (years > 0) {
    return `成为会员第${years}年${days}天`;
  }
  return `成为会员第${totalDays}天`;
});

// 资产数据
const assets = ref({
  coupons: 0,
  cards: 0,
  wallet: 0.00,
  points: 0
});

// 加载用户积分
const loadUserPoints = () => {
  if (isLoggedIn.value && userStore.userInfo) {
    assets.value.points = userStore.userInfo.currentPoints || 0;
  }
};

// 会员等级相关展示（使用后端返回的数据）
const vipLevelText = computed(() => {
  const level = userStore.userInfo?.level;
  return level || 'S1';
});

const vipProgressText = computed(() => {
  const info = userStore.userInfo || {};
  const current = info.currentPoints ?? 0;
  const target = info.nextLevelPoints ?? 0;
  const nextLevel = info.nextLevel || '下一等级';

  if (!target || target <= 0) {
    return '已达到最高会员等级';
  }
  const remain = Math.max(target - current, 0);
  return `再获得 ${remain} 积分即可升级到 ${nextLevel}`;
});

const vipCardStyle = computed(() => {
  const level = userStore.userInfo?.level || 'S1';
  const match = String(level).match(/S(\d+)/i);
  const num = match ? parseInt(match[1], 10) : 1;

  // 不同等级对应不同颜色渐变背景
  switch (true) {
    case num >= 5:
      return { background: 'linear-gradient(135deg, #f48fb1 0%, #e91e63 100%)' };
    case num === 4:
      return { background: 'linear-gradient(135deg, #ffcc80 0%, #ff9800 100%)' };
    case num === 3:
      return { background: 'linear-gradient(135deg, #90caf9 0%, #2196f3 100%)' };
    case num === 2:
      return { background: 'linear-gradient(135deg, #a5d6a7 0%, #4caf50 100%)' };
    default:
      return { background: 'linear-gradient(135deg, #e0e0e0 0%, #b0bec5 100%)' };
  }
});

// 加载钱包余额
const loadWalletBalance = async () => {
  if (!isLoggedIn.value) {
    assets.value.wallet = 0.00;
    return;
  }
  
  const walletInfo = await getWalletInfoSafe();
  if (walletInfo) {
    assets.value.wallet = parseFloat(walletInfo.balance || 0);
  }
};

// 显示资产值（未登录显示 "-"）
const displayAssetValue = (value, isDecimal = false) => {
  if (!isLoggedIn.value) {
    return '-';
  }
  return isDecimal ? value.toFixed(2) : value;
};

// 点击用户名
const handleUserNameClick = () => {
  if (!isLoggedIn.value) {
    showLoginModal.value = true;
  } else {
    // 已登录，跳转到个人资料页面
    router.push('/profile');
  }
};

// 点击资产项
const handleAssetClick = (type) => {
  if (!isLoggedIn.value) {
    showLoginModal.value = true;
    return;
  }
  
  // 已登录，根据不同的资产类型跳转
  switch(type) {
    case 'wallet':
      // 跳转到钱包页面（如果有的话）
      showToast('钱包管理功能开发中');
      break;
    case 'coupons':
      showToast('优惠券功能开发中');
      break;
    case 'cards':
      showToast('礼品卡功能开发中');
      break;
    case 'points':
      showToast('积分功能开发中');
      break;
  }
};

// 菜单点击处理
const handleMenuClick = (type) => {
  // 未登录时，所有菜单项都跳转到登录页面
  if (!isLoggedIn.value) {
    showLoginModal.value = true;
    return;
  }

  // 已登录，根据不同的菜单项跳转到不同的页面
  console.log('点击菜单:', type);
  switch(type) {
    case 'orders':
      router.push('/my-orders');
      break;
    case 'browse':
      router.push('/browse-history');
      break;
    case 'favorites':
      router.push('/my-favorites');
      break;
    case 'service':
      // router.push('/service');
      console.log('跳转到我的客服');
      break;
  }
};

// 关闭登录弹窗
const closeLoginModal = () => {
  showLoginModal.value = false;
};

// 监听登录状态变化
watch(() => userStore.isLoggedIn, (newVal) => {
  if (newVal) {
    // 登录成功后加载钱包余额和积分
    loadWalletBalance();
    loadUserPoints();
  } else {
    // 退出登录时清空资产数据
    assets.value.wallet = 0.00;
    assets.value.coupons = 0;
    assets.value.cards = 0;
    assets.value.points = 0;
  }
});

onMounted(() => {
  // 页面加载时，如果已登录，加载钱包余额和积分
  if (isLoggedIn.value) {
    loadWalletBalance();
    loadUserPoints();
  }
});
</script>

<style scoped>
.my-view {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 1.6rem;
}

/* 头部用户信息 */
.header {
  background-color: #fff;
  padding: 40px 20px 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.user-info {
  flex: 1;
  cursor: pointer;
}

.user-name {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.user-name .arrow-right {
  font-size: 28px;
  margin-left: 8px;
  color: #999;
  font-style: normal;
}

.member-days {
  font-size: 14px;
  color: #666;
  font-weight: 400;
}

.qr-icon {
  width: 32px;
  height: 32px;
  color: #333;
  cursor: pointer;
}

/* VIP卡片 */
.vip-card {
  margin: 16px 20px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 16px;
  padding: 32px 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
}

.vip-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(255, 215, 0, 0.15) 0%, transparent 70%);
  border-radius: 50%;
}

.vip-card::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -10%;
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(255, 215, 0, 0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.vip-title {
  font-size: 32px;
  font-weight: 600;
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 50%, #ffd700 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  letter-spacing: 3px;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  position: relative;
  z-index: 1;
}

.vip-progress {
  margin-top: 12px;
  position: relative;
  z-index: 1;
}

.progress-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 400;
  line-height: 1.6;
}

/* 资产统计 */
.assets-section {
  background-color: #fff;
  padding: 24px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.asset-item {
  flex: 1;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s;
}

.asset-item:active {
  transform: scale(0.95);
}

.asset-value {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  font-family: 'DIN Alternate', 'Arial', sans-serif;
}

.asset-label {
  font-size: 13px;
  color: #666;
  font-weight: 400;
}

/* 分割线 */
.divider {
  height: 1px;
  background-color: #e5e5e5;
  margin: 0 20px;
}

/* 菜单列表 */
.menu-list {
  background-color: #fff;
  margin-top: 12px;
}

.menu-item {
  padding: 18px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background-color 0.2s;
  position: relative;
}

.menu-item:active {
  background-color: #f8f8f8;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-text {
  font-size: 16px;
  color: #333;
  font-weight: 400;
  flex: 1;
}

.badge-item {
  position: relative;
}

.badge {
  position: absolute;
  left: 90px;
  top: 50%;
  transform: translateY(-50%);
  background-color: #ff4d4f;
  color: #fff;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
}

.arrow-right {
  font-size: 20px;
  color: #ccc;
  font-style: normal;
  font-weight: 300;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .user-name {
    font-size: 28px;
  }
  
  .vip-title {
    font-size: 24px;
  }
  
  .asset-value {
    font-size: 28px;
  }
}
</style>
