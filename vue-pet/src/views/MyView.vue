<template>
  <div class="my-view">
    <!-- 头部用户信息 -->
    <div class="header">
      <div class="user-info" @click="handleUserNameClick">
        <div class="user-name">
          {{ displayName }}
          <i class="arrow-right">›</i>
        </div>
        <div class="member-days" v-if="isLoggedIn">成为会员第5年219天</div>
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
    <div class="vip-card" v-if="isLoggedIn">
      <div class="vip-title">见习贵宾</div>
      <div class="vip-progress">
        <div class="progress-text">累计10成长值成为「进阶贵宾」</div>
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

      <div class="menu-item" @click="handleMenuClick('booking')">
        <span class="menu-text">团餐预定</span>
        <i class="arrow-right">›</i>
      </div>

      <div class="menu-item" @click="handleMenuClick('exchange')">
        <span class="menu-text">兑换中心</span>
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

// 资产数据
const assets = ref({
  coupons: 0,
  cards: 0,
  wallet: 0.00,
  points: 0
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
    case 'booking':
      // router.push('/booking');
      console.log('跳转到团餐预定');
      break;
    case 'exchange':
      // router.push('/exchange');
      console.log('跳转到兑换中心');
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
    // 登录成功后加载钱包余额
    loadWalletBalance();
  } else {
    // 退出登录时清空资产数据
    assets.value.wallet = 0.00;
    assets.value.coupons = 0;
    assets.value.cards = 0;
    assets.value.points = 0;
  }
});

onMounted(() => {
  // 页面加载时，如果已登录，加载钱包余额
  if (isLoggedIn.value) {
    loadWalletBalance();
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
  background: linear-gradient(135deg, #e8dcc4 0%, #d4c5a9 100%);
  border-radius: 12px;
  padding: 30px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.vip-title {
  font-size: 28px;
  font-weight: 500;
  color: #4a4a4a;
  margin-bottom: 40px;
  letter-spacing: 2px;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.vip-progress {
  margin-top: 20px;
}

.progress-text {
  font-size: 15px;
  color: #666;
  font-weight: 400;
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
