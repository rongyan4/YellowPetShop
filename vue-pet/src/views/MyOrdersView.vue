<template>
  <div class="my-orders-view">
    <!-- 头部 -->
    <div class="header">
      <van-icon name="arrow-left" class="back-icon" @click="goBack" />
      <h1 class="title">我的订单</h1>
    </div>

    <!-- 订单状态标签 -->
    <div class="tabs-wrapper">
      <tabs v-model:active="activeTabIndex" @change="onTabChange">
        <tab 
          v-for="(item, index) in tabList" 
          :title="item.title"
          :key="item.name">
        </tab>
      </tabs>
    </div>

    <!-- 订单列表 -->
    <div class="orders-content">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <!-- 有订单 -->
          <div v-if="orderList.length > 0" class="order-list">
            <div 
              v-for="order in orderList" 
              :key="order.id" 
              class="order-card"
            >
              <!-- 订单头部 - 可点击跳转到订单详情 -->
              <div class="order-header" @click="viewOrderDetail(order.id)">
                <div class="order-info">
                  <span class="order-sn">订单号: {{ order.orderSn }}</span>
                  <span class="order-time">{{ formatTime(order.createTime) }}</span>
                </div>
                <div class="order-status-wrapper">
                  <span class="order-status" :class="getStatusClass(order.status)">
                    {{ order.statusText }}
                  </span>
                  <!-- 待支付订单显示倒计时 -->
                  <span v-if="order.status === 'PENDING' && order.remainingTime != null && order.remainingTime > 0" class="countdown">
                    <van-icon name="clock-o" />
                    {{ formatCountdown(order.remainingTime) }}
                  </span>
                  <span v-else-if="order.status === 'PENDING' && order.remainingTime != null && order.remainingTime <= 0" class="timeout-tip">
                    已超时
                  </span>
                </div>
              </div>

              <!-- 订单商品列表 -->
              <div class="order-goods">
                <div 
                  v-for="item in order.items" 
                  :key="item.id"
                  class="goods-item"
                  @click="goToGoodDetail(item.commodityId)"
                >
                  <img :src="item.commodityPic" :alt="item.commodityName" class="goods-image">
                  <div class="goods-info">
                    <div class="goods-name">{{ item.commodityName }}</div>
                    <div class="goods-bottom">
                      <span class="goods-price">¥{{ item.commodityPrice }}</span>
                      <span class="goods-quantity">x{{ item.quantity }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 订单金额 -->
              <div class="order-amount">
                <span class="amount-label">实付款：</span>
                <span class="amount-value">¥{{ order.payAmount }}</span>
              </div>

              <!-- 订单操作 -->
              <div class="order-actions">
                <van-button 
                  v-if="order.status === 'PENDING'" 
                  size="small" 
                  plain
                  @click="cancelOrder(order.id)"
                >
                  取消订单
                </van-button>
                <van-button 
                  v-if="order.status === 'CANCELLED' || order.status === 'COMPLETED'" 
                  size="small" 
                  plain
                  @click="deleteOrder(order.id)"
                >
                  删除订单
                </van-button>
                <van-button 
                  v-if="order.status === 'COMPLETED'" 
                  size="small" 
                  plain
                  @click="goToComment(order.id)"
                >
                  评价
                </van-button>
                <van-button 
                  v-if="order.status === 'PENDING'" 
                  size="small" 
                  type="primary"
                  @click="payOrder(order)"
                >
                  去支付
                </van-button>
                <van-button 
                  v-if="order.status === 'SHIPPED'" 
                  size="small" 
                  type="primary"
                  @click="confirmReceipt(order.id)"
                >
                  确认收货
                </van-button>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else-if="!loading" class="empty-orders">
            <van-empty description="暂无订单" />
            <van-button type="primary" round @click="goShopping">去逛逛</van-button>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, onBeforeUnmount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { showToast, showConfirmDialog } from 'vant';
import { getOrderListSafe, cancelOrderSafe, deleteOrderSafe, confirmReceiptSafe } from '@/api/order';
import { Tab, Tabs } from 'vant';
import { saveScrollPosition, restoreScrollPosition } from '@/utils/scrollPosition';

const router = useRouter();
const route = useRoute();

// 标签列表
const tabList = ref([
  { name: 'ALL', title: '全部' },
  { name: 'PENDING', title: '待付款' },
  { name: 'PAID', title: '待发货' },
  { name: 'SHIPPED', title: '运输中' },
  { name: 'COMPLETED', title: '已完成' }
]);

// 当前激活的标签索引
const activeTabIndex = ref(0);

// 当前激活的标签名称
const activeTab = ref('ALL');

// 订单列表
const orderList = ref([]);

// 加载状态
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);

// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);

// 倒计时定时器
let countdownTimer = null;

// 订单超时时间（毫秒）
const ORDER_TIMEOUT = 5 * 60 * 1000; // 5分钟

// 状态样式映射
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

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// 加载订单列表
const loadOrders = async (isRefresh = false) => {
  try {
    loading.value = true;
    
    // 传递状态参数给后端
    const status = activeTab.value;
    const data = await getOrderListSafe(status);
    
    if (data && Array.isArray(data)) {
      // 为每个待支付订单计算剩余时间
      data.forEach(order => {
        if (order.status === 'PENDING') {
          const createTime = new Date(order.createTime).getTime();
          const now = Date.now();
          const elapsed = now - createTime;
          order.remainingTime = Math.max(0, ORDER_TIMEOUT - elapsed);
        } else {
          // 非待支付订单，设置为 null
          order.remainingTime = null;
        }
      });
      
      // 按创建时间降序排序
      data.sort((a, b) => {
        return new Date(b.createTime).getTime() - new Date(a.createTime).getTime();
      });
      
      if (isRefresh) {
        orderList.value = data;
      } else {
        orderList.value = [...orderList.value, ...data];
      }
      
      // 简化处理：一次性加载所有订单
      finished.value = true;
      
      // 启动倒计时
      startCountdown();
    } else {
      orderList.value = [];
      finished.value = true;
    }
  } catch (error) {
    console.error('加载订单失败:', error);
    showToast('加载订单失败');
    orderList.value = [];
    finished.value = true;
  } finally {
    loading.value = false;
    refreshing.value = false;
  }
};

// 标签切换
const onTabChange = (index) => {
  activeTab.value = tabList.value[index].name;
  orderList.value = [];
  finished.value = false;
  currentPage.value = 1;
  loadOrders(true);
};

// 下拉刷新
const onRefresh = () => {
  orderList.value = [];
  finished.value = false;
  currentPage.value = 1;
  loadOrders(true);
};

// 上拉加载
const onLoad = () => {
  if (orderList.value.length === 0) {
    loadOrders();
  }
};

// 查看订单详情
const viewOrderDetail = (orderId) => {
  // 保存当前滚动位置和标签页状态
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
  sessionStorage.setItem('myOrdersActiveTab', activeTabIndex.value.toString());
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
      onRefresh();
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
      onRefresh();
    }
  }).catch(() => {
    // 用户取消
  });
};

// 去支付
const payOrder = (order) => {
  // 保存当前滚动位置和标签页状态
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
  sessionStorage.setItem('myOrdersActiveTab', activeTabIndex.value.toString());
  // 跳转到订单详情页进行支付
  router.push(`/order/detail/${order.id}`);
};

// 去评价
const goToComment = (orderId) => {
  // 保存当前滚动位置和标签页状态
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
  sessionStorage.setItem('myOrdersActiveTab', activeTabIndex.value.toString());
  router.push(`/order/comment/${orderId}`);
};

// 跳转到商品详情
const goToGoodDetail = (commodityId) => {
  // 保存当前滚动位置和标签页状态
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
  sessionStorage.setItem('myOrdersActiveTab', activeTabIndex.value.toString());
  router.push(`/good-details?id=${commodityId}`);
};

// 确认收货
const confirmReceipt = (orderId) => {
  showConfirmDialog({
    title: '确认收货',
    message: '确认已收到商品吗？',
  }).then(async () => {
    const result = await confirmReceiptSafe(orderId);
    if (result !== null) {
      showToast('确认收货成功');
      onRefresh();
    }
  }).catch(() => {
    // 用户取消
  });
};

// 去购物
const goShopping = () => {
  router.push('/shopping');
};

// 返回
const goBack = () => {
  router.back();
};

// 启动倒计时
const startCountdown = () => {
  // 清除旧的定时器
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
  
  // 检查是否有待支付订单
  const hasPendingOrders = orderList.value.some(
    order => order.status === 'PENDING' && order.remainingTime != null && order.remainingTime > 0
  );
  
  if (!hasPendingOrders) {
    return;
  }
  
  // 每秒更新一次倒计时
  countdownTimer = setInterval(() => {
    let hasTimeout = false;
    
    orderList.value.forEach(order => {
      if (order.status === 'PENDING' && order.remainingTime != null && order.remainingTime > 0) {
        order.remainingTime -= 1000;
        
        // 如果倒计时结束，标记需要刷新
        if (order.remainingTime <= 0) {
          hasTimeout = true;
        }
      }
    });
    
    // 如果有订单超时，刷新列表
    if (hasTimeout) {
      setTimeout(() => {
        onRefresh();
      }, 1000);
    }
  }, 1000);
};

// 格式化倒计时
const formatCountdown = (milliseconds) => {
  if (milliseconds == null || milliseconds < 0) {
    return '0:00';
  }
  const totalSeconds = Math.floor(milliseconds / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
};

onMounted(() => {
  // 恢复标签页状态
  const savedTab = sessionStorage.getItem('myOrdersActiveTab');
  if (savedTab !== null) {
    activeTabIndex.value = parseInt(savedTab);
    activeTab.value = tabList.value[activeTabIndex.value].name;
  }
  
  loadOrders();
  
  // 恢复滚动位置
  restoreScrollPosition(route.path);
});

onUnmounted(() => {
  // 清除定时器
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
});

// 页面卸载前保存滚动位置
onBeforeUnmount(() => {
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
});
</script>

<style scoped>
.my-orders-view {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
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
.orders-content {
  padding: 12px;
}

/* 订单列表 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f5f5f5;
  background-color: #fafafa;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-sn {
  font-size: 13px;
  color: #666;
}

.order-time {
  font-size: 12px;
  color: #999;
}

.order-status-wrapper {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.order-status {
  font-size: 14px;
  font-weight: 600;
}

.countdown {
  font-size: 12px;
  color: #ff9800;
  display: flex;
  align-items: center;
  gap: 4px;
}

.timeout-tip {
  font-size: 12px;
  color: #999;
}

.status-pending {
  color: #ff9800;
}

.status-paid {
  color: #2196f3;
}

/* 标签样式 */
.tabs-wrapper {
  background-color: #fff;
  position: sticky;
  top: 56px;
  z-index: 99;
}

:deep(.van-tab__text) {
  font-size: .3733rem;
}

:deep(.van-tabs__line) {
  width: 1.0667rem;
  height: .0533rem;
  background-color: #2c3e50;
  border-radius: .0267rem;
}

:deep(.van-tabs__wrap) {
  height: 1.1733rem;
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
  background-color: #f0f0f0;
}

/* 订单商品 */
.order-goods {
  padding: 12px 16px;
}

.goods-item {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.goods-item:hover {
  background-color: #fafafa;
}

.goods-item:last-child {
  margin-bottom: 0;
}

.goods-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  background-color: #f5f5f5;
}

.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-name {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goods-price {
  font-size: 16px;
  color: #ff6b6b;
  font-weight: 600;
}

.goods-quantity {
  font-size: 14px;
  color: #999;
}

/* 订单金额 */
.order-amount {
  padding: 12px 16px;
  text-align: right;
  border-top: 1px solid #f5f5f5;
}

.amount-label {
  font-size: 14px;
  color: #666;
}

.amount-value {
  font-size: 18px;
  color: #ff6b6b;
  font-weight: 600;
  margin-left: 4px;
}

/* 订单操作 */
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #f5f5f5;
}

/* 空状态 */
.empty-orders {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.empty-orders .van-button {
  margin-top: 20px;
}
</style>
