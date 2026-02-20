<template>
  <div class="order-list-view">
    <!-- 头部 -->
    <div class="header">
      <van-icon name="arrow-left" class="back-icon" @click="goBack" />
      <h1 class="title">我的订单</h1>
    </div>

    <!-- 订单列表 -->
    <div class="order-content" v-if="isLoggedIn">
      <!-- 有订单 -->
      <div v-if="orders.length > 0" class="order-list">
        <div 
          v-for="order in orders" 
          :key="order.id" 
          class="order-item"
        >
          <!-- 订单头部 - 可点击跳转到订单详情 -->
          <div class="order-header" @click="viewOrderDetail(order.id)">
            <span class="order-sn">订单号: {{ order.orderSn }}</span>
            <span class="order-status" :class="getStatusClass(order.status)">
              {{ order.statusText }}
            </span>
          </div>

          <!-- 订单商品列表 -->
          <div class="order-items">
            <div 
              v-for="item in order.items" 
              :key="item.id"
              class="order-item-detail"
              @click="goToGoodDetail(item.commodityId)"
            >
              <img :src="item.commodityPic" :alt="item.commodityName" class="item-image">
              <div class="item-info">
                <div class="item-name">{{ item.commodityName }}</div>
                <div class="item-bottom">
                  <span class="item-price">¥{{ item.commodityPrice }}</span>
                  <span class="item-quantity">x{{ item.quantity }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 订单底部 -->
          <div class="order-footer">
            <div class="order-total">
              <span class="total-label">实付款：</span>
              <span class="total-amount">¥{{ order.payAmount }}</span>
            </div>
            <div class="order-actions">
              <van-button 
                v-if="order.status === 'PENDING'" 
                size="small" 
                plain
                @click.stop="cancelOrder(order.id)"
              >
                取消订单
              </van-button>
              <van-button 
                v-if="order.status === 'CANCELLED' || order.status === 'COMPLETED'" 
                size="small" 
                plain
                @click.stop="deleteOrder(order.id)"
              >
                删除订单
              </van-button>
              <van-button 
                v-if="order.status === 'COMPLETED'" 
                size="small" 
                plain
                @click.stop="goToComment(order.id)"
              >
                评价
              </van-button>
              <van-button 
                v-if="order.status === 'PENDING'" 
                size="small" 
                type="primary"
                @click.stop="payOrder(order.id)"
              >
                去支付
              </van-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 空订单 -->
      <div v-else class="empty-order">
        <div class="empty-icon">
          <svg viewBox="0 0 120 120" width="120" height="120">
            <rect x="30" y="40" width="60" height="50" fill="#f5f5f5" stroke="#ddd" stroke-width="2" rx="4"/>
            <line x1="30" y1="55" x2="90" y2="55" stroke="#ddd" stroke-width="2"/>
            <circle cx="45" cy="70" r="3" fill="#999"/>
            <circle cx="60" cy="70" r="3" fill="#999"/>
            <circle cx="75" cy="70" r="3" fill="#999"/>
          </svg>
        </div>
        <p class="empty-text">暂无订单</p>
        <p class="empty-subtext">快去选购心仪的商品吧~</p>
        <button class="go-shopping-btn" @click="goShopping">去逛逛</button>
      </div>
    </div>

    <!-- 未登录状态 -->
    <div v-else class="not-logged-in">
      <div class="login-prompt">
        <div class="login-icon">
          <svg viewBox="0 0 100 100" width="100" height="100">
            <circle cx="50" cy="35" r="20" fill="#ddd"/>
            <path d="M20 80 Q20 60 50 60 Q80 60 80 80 Z" fill="#ddd"/>
          </svg>
        </div>
        <p class="login-text">登录后查看订单</p>
        <button class="login-btn" @click="goToLogin">立即登录</button>
      </div>
    </div>

    <!-- TabBar -->
    <TabBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useUserStore } from '@/stores/user';
import { useRouter, useRoute } from 'vue-router';
import TabBar from '@/components/TabBar.vue';
import { getOrderListSafe, cancelOrderSafe, deleteOrderSafe } from '@/api/order';
import { showToast, showConfirmDialog } from 'vant';
import { saveScrollPosition, restoreScrollPosition } from '@/utils/scrollPosition';

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();

// 登录状态
const isLoggedIn = computed(() => userStore.isLoggedIn);

// 订单列表
const orders = ref([]);
const loading = ref(false);

// 加载订单列表
const loadOrders = async () => {
  if (!isLoggedIn.value) {
    orders.value = [];
    return;
  }
  
  try {
    loading.value = true;
    const data = await getOrderListSafe();
    
    if (data) {
      orders.value = data;
      console.log('订单列表加载成功:', orders.value);
    }
  } catch (error) {
    console.error('加载订单列表失败:', error);
    showToast('加载订单列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取状态样式类
const getStatusClass = (status) => {
  const classMap = {
    'PENDING': 'status-pending',
    'PAID': 'status-paid',
    'SHIPPED': 'status-shipped',
    'COMPLETED': 'status-completed',
    'CANCELLED': 'status-cancelled'
  };
  return classMap[status] || '';
};

// 查看订单详情
const viewOrderDetail = (orderId) => {
  // 保存当前滚动位置
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
  router.push(`/order/detail/${orderId}`);
};

// 取消订单
const cancelOrder = async (orderId) => {
  showConfirmDialog({
    title: '确认取消',
    message: '确定要取消该订单吗？',
  }).then(async () => {
    const result = await cancelOrderSafe(orderId);
    if (result !== null) {
      showToast('订单已取消');
      loadOrders();
    }
  }).catch(() => {
    // 用户取消
  });
};

// 删除订单
const deleteOrder = async (orderId) => {
  showConfirmDialog({
    title: '确认删除',
    message: '确定要删除该订单吗？',
  }).then(async () => {
    const result = await deleteOrderSafe(orderId);
    if (result !== null) {
      showToast('订单已删除');
      loadOrders();
    }
  }).catch(() => {
    // 用户取消
  });
};

// 支付订单
const payOrder = (orderId) => {
  console.log('支付订单:', orderId);
  showToast('支付功能开发中');
};

// 去评价
const goToComment = (orderId) => {
  // 保存当前滚动位置
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
  router.push(`/order/comment/${orderId}`);
};

// 跳转到商品详情
const goToGoodDetail = (commodityId) => {
  // 保存当前滚动位置
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
  router.push(`/good-details?id=${commodityId}`);
};

// 返回
const goBack = () => {
  router.back();
};

// 去购物
const goShopping = () => {
  router.push('/shopping');
};

// 去登录
const goToLogin = () => {
  router.push('/my');
};

onMounted(() => {
  loadOrders();
  // 恢复滚动位置
  restoreScrollPosition(route.path);
});

// 页面卸载前保存滚动位置
onBeforeUnmount(() => {
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
});
</script>

<style scoped>
.order-list-view {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 1.6rem;
}

/* 头部 */
.header {
  position: sticky;
  top: 0;
  background-color: #fff;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.back-icon {
  font-size: 20px;
  color: #333;
  cursor: pointer;
  margin-right: 12px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

/* 订单内容 */
.order-content {
  padding: 12px;
}

/* 订单列表 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.3s;
}

.order-item:active {
  transform: scale(0.98);
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f5f5f5;
}

.order-sn {
  font-size: 13px;
  color: #666;
}

.order-status {
  font-size: 14px;
  font-weight: 500;
}

.status-pending {
  color: #ff9800;
}

.status-paid {
  color: #2196f3;
}

.status-shipped {
  color: #9c27b0;
}

.status-completed {
  color: #4caf50;
}

.status-cancelled {
  color: #999;
}

/* 订单头部可点击 */
.order-header {
  cursor: pointer;
  transition: background-color 0.2s;
}

.order-header:hover {
  background-color: #fafafa;
}

/* 订单商品 */
.order-items {
  padding: 12px 16px;
}

.order-item-detail {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.order-item-detail:hover {
  background-color: #fafafa;
}

.order-item-detail:last-child {
  margin-bottom: 0;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  background-color: #f5f5f5;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-name {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  font-size: 16px;
  color: #ff6b6b;
  font-weight: 600;
}

.item-quantity {
  font-size: 14px;
  color: #999;
}

/* 订单底部 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-top: 1px solid #f5f5f5;
}

.order-total {
  display: flex;
  align-items: baseline;
}

.total-label {
  font-size: 13px;
  color: #666;
}

.total-amount {
  font-size: 18px;
  color: #ff6b6b;
  font-weight: 600;
  margin-left: 4px;
}

.order-actions {
  display: flex;
  gap: 8px;
}

/* 空订单 */
.empty-order {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.empty-icon {
  margin-bottom: 24px;
  opacity: 0.6;
}

.empty-text {
  font-size: 16px;
  color: #666;
  margin: 0 0 8px;
  font-weight: 500;
}

.empty-subtext {
  font-size: 14px;
  color: #999;
  margin: 0 0 32px;
}

.go-shopping-btn {
  padding: 12px 48px;
  background: linear-gradient(135deg, #8bc34a 0%, #7cb342 100%);
  color: #fff;
  border: none;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(124, 179, 66, 0.3);
}

.go-shopping-btn:active {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(124, 179, 66, 0.3);
}

/* 未登录状态 */
.not-logged-in {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.login-prompt {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-icon {
  margin-bottom: 24px;
  opacity: 0.5;
}

.login-text {
  font-size: 16px;
  color: #666;
  margin: 0 0 32px;
}

.login-btn {
  padding: 12px 48px;
  background: linear-gradient(135deg, #8bc34a 0%, #7cb342 100%);
  color: #fff;
  border: none;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(124, 179, 66, 0.3);
}

.login-btn:active {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(124, 179, 66, 0.3);
}
</style>
