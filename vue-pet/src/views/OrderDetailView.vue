<template>
  <div class="order-detail-view">
    <!-- 头部 -->
    <div class="header">
      <van-icon name="arrow-left" class="back-icon" @click="goBack" />
      <h1 class="title">订单详情</h1>
    </div>

    <div v-if="orderDetail" class="detail-content">
      <!-- 订单状态 -->
      <div class="status-section">
        <div class="status-icon">
          <van-icon v-if="orderDetail.status === 'PENDING'" name="clock-o" size="48" color="#ff9800" />
          <van-icon v-else-if="orderDetail.status === 'PAID'" name="logistics" size="48" color="#2196f3" />
          <van-icon v-else-if="orderDetail.status === 'SHIPPED'" name="logistics" size="48" color="#2196f3" />
          <van-icon v-else-if="orderDetail.status === 'COMPLETED'" name="success" size="48" color="#4caf50" />
          <van-icon v-else name="cross" size="48" color="#999" />
        </div>
        <div class="status-text">{{ orderDetail.statusText }}</div>
        <div v-if="orderDetail.status === 'PENDING'" class="status-tip">
          <template v-if="remainingTime > 0">
            <van-icon name="clock-o" />
            剩余支付时间：{{ formatCountdown(remainingTime) }}
          </template>
          <template v-else>
            订单已超时
          </template>
        </div>
      </div>

      <!-- 收货地址 -->
      <div class="address-section">
        <div class="section-title">
          <van-icon name="location-o" />
          <span>收货信息</span>
        </div>
        <div class="address-content">
          <div class="receiver-info">
            <span class="receiver-name">{{ orderDetail.receiverName }}</span>
            <span class="receiver-phone">{{ orderDetail.receiverPhone }}</span>
          </div>
          <div class="address-detail">{{ orderDetail.receiverAddress }}</div>
        </div>
      </div>

          <!-- 商品信息 -->
          <div class="goods-section">
            <div class="section-title">
              <van-icon name="bag-o" />
              <span>商品信息</span>
            </div>
            <div class="goods-list">
              <div 
                v-for="item in orderDetail.items" 
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
          </div>

      <!-- 订单信息 -->
      <div class="order-info-section">
        <div class="section-title">
          <van-icon name="orders-o" />
          <span>订单信息</span>
        </div>
        <van-cell-group :border="false">
          <van-cell title="订单编号" :value="orderDetail.orderSn" />
          <van-cell title="创建时间" :value="formatTime(orderDetail.createTime)" />
          <van-cell v-if="orderDetail.payTime" title="支付时间" :value="formatTime(orderDetail.payTime)" />
          <van-cell v-if="orderDetail.paymentMethod" title="支付方式" :value="getPaymentMethodText(orderDetail.paymentMethod)" />
          <van-cell title="商品金额" :value="`¥${orderDetail.totalAmount}`" />
          <van-cell title="运费" :value="`¥${orderDetail.postage}`" />
          <van-cell title="实付款" :value="`¥${orderDetail.payAmount}`" value-class="pay-amount-value" />
          <van-cell v-if="orderDetail.remark" title="订单备注" :value="orderDetail.remark" />
        </van-cell-group>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div v-if="orderDetail" class="bottom-bar">
      <van-button 
        v-if="orderDetail.status === 'PENDING'" 
        plain
        @click="cancelOrder"
      >
        取消订单
      </van-button>
      <van-button 
        v-if="orderDetail.status === 'PENDING'" 
        type="primary"
        @click="showPaymentMethodPopup = true"
      >
        立即支付
      </van-button>
      <van-button 
        v-if="orderDetail.status === 'SHIPPED'" 
        type="primary"
        @click="confirmReceipt"
      >
        确认收货
      </van-button>
      <van-button 
        v-if="orderDetail.status === 'COMPLETED'" 
        type="primary"
        @click="goToComment"
      >
        评价订单
      </van-button>
    </div>

    <!-- 支付方式选择弹窗 -->
    <van-popup v-model:show="showPaymentMethodPopup" position="bottom" round>
      <div class="payment-method-popup">
        <div class="popup-header">
          <span class="popup-title">选择支付方式</span>
          <van-icon name="cross" @click="showPaymentMethodPopup = false" />
        </div>
        
        <div class="payment-methods">
          <div 
            class="payment-method-item"
            :class="{ 
              active: selectedPaymentMethod === 'WALLET',
              disabled: !isBalanceSufficient
            }"
            @click="selectPaymentMethod('WALLET')"
          >
            <div class="method-info">
              <van-icon 
                name="balance-o" 
                size="24" 
                :color="isBalanceSufficient ? '#07c160' : '#999'" 
              />
              <span class="method-name">钱包支付</span>
              <span 
                class="wallet-balance"
                :class="{ insufficient: !isBalanceSufficient }"
              >
                余额：¥{{ walletInfo ? walletInfo.balance : '0.00' }}
              </span>
            </div>
            <van-radio 
              :model-value="selectedPaymentMethod === 'WALLET'" 
              :disabled="!isBalanceSufficient"
            />
          </div>
          
          <div 
            class="payment-method-item disabled"
            :class="{ active: selectedPaymentMethod === 'WECHAT' }"
            @click="selectPaymentMethod('WECHAT')"
          >
            <div class="method-info">
              <van-icon name="wechat-pay" size="24" color="#09bb07" />
              <span class="method-name">微信支付</span>
              <span class="method-tip">暂不支持</span>
            </div>
            <van-radio :model-value="selectedPaymentMethod === 'WECHAT'" disabled />
          </div>
          
          <div 
            class="payment-method-item disabled"
            :class="{ active: selectedPaymentMethod === 'ALIPAY' }"
            @click="selectPaymentMethod('ALIPAY')"
          >
            <div class="method-info">
              <van-icon name="alipay" size="24" color="#1677ff" />
              <span class="method-name">支付宝支付</span>
              <span class="method-tip">暂不支持</span>
            </div>
            <van-radio :model-value="selectedPaymentMethod === 'ALIPAY'" disabled />
          </div>
        </div>
        
        <div class="payment-amount">
          <span>支付金额：</span>
          <span class="amount">¥{{ orderDetail.payAmount }}</span>
        </div>
        
        <van-button 
          type="primary" 
          block 
          @click="confirmPaymentMethod"
          :disabled="!selectedPaymentMethod || !isBalanceSufficient"
        >
          确认支付
        </van-button>
      </div>
    </van-popup>

    <!-- 支付密码弹窗 -->
    <van-popup v-model:show="showPaymentPopup" :close-on-click-overlay="false">
      <div class="payment-popup">
        <div class="popup-header">
          <span class="popup-title">输入支付密码</span>
          <van-icon name="cross" @click="cancelPayment" />
        </div>
        
        <div class="wallet-balance">
          <span>钱包余额：¥{{ walletInfo ? walletInfo.balance : '0.00' }}</span>
        </div>
        
        <div class="pay-amount">
          <span class="amount-label">支付金额</span>
          <span class="amount-value">¥{{ orderDetail.payAmount }}</span>
        </div>
        
        <!-- 密码输入框 -->
        <div class="password-input">
          <div 
            v-for="i in 6" 
            :key="i" 
            class="password-box"
            :class="{ active: password.length === i - 1 }"
          >
            <span v-if="password.length >= i" class="dot">●</span>
          </div>
        </div>
        
        <div v-if="passwordError" class="password-error">{{ passwordError }}</div>
        
        <!-- 自定义数字键盘 -->
        <div class="custom-keyboard">
          <div class="keyboard-row" v-for="row in keyboard" :key="row.join()">
            <div 
              v-for="key in row" 
              :key="key"
              class="keyboard-key"
              :class="{ 
                backspace: key === 'backspace',
                clear: key === 'clear',
                disabled: key === '' 
              }"
              @click="handleKeyPress(key)"
            >
              <van-icon v-if="key === 'backspace'" name="arrow-left" />
              <van-icon v-else-if="key === 'clear'" name="delete-o" />
              <span v-else>{{ key }}</span>
            </div>
          </div>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { showToast, showConfirmDialog } from 'vant';
import { getOrderDetailSafe, cancelOrderSafe, confirmReceiptSafe, payOrderSafe } from '@/api/order';
import { getWalletInfoSafe } from '@/api/payment';
import { saveScrollPosition } from '@/utils/scrollPosition';

const router = useRouter();
const route = useRoute();

// 订单详情
const orderDetail = ref(null);

// 钱包信息
const walletInfo = ref(null);

// 支付方式选择
const showPaymentMethodPopup = ref(false);
const selectedPaymentMethod = ref('');

// 余额是否充足
const isBalanceSufficient = computed(() => {
  if (!walletInfo.value || !orderDetail.value) return false;
  return parseFloat(walletInfo.value.balance) >= parseFloat(orderDetail.value.payAmount);
});

// 支付密码相关
const showPaymentPopup = ref(false);
const password = ref('');
const passwordError = ref('');
const errorCount = ref(0);

// 倒计时相关
const remainingTime = ref(0);
let countdownTimer = null;
const ORDER_TIMEOUT = 5 * 60 * 1000; // 5分钟

// 数字键盘布局
const keyboard = [
  ['1', '2', '3'],
  ['4', '5', '6'],
  ['7', '8', '9'],
  ['clear', '0', 'backspace']
];

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

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  const methodMap = {
    'WALLET': '钱包支付',
    'WECHAT': '微信支付',
    'ALIPAY': '支付宝支付'
  };
  return methodMap[method] || method;
};

// 加载订单详情
const loadOrderDetail = async () => {
  const orderId = route.params.id;
  if (!orderId) {
    showToast('订单ID不存在');
    router.back();
    return;
  }
  
  const data = await getOrderDetailSafe(orderId);
  if (data) {
    orderDetail.value = data;
    
    // 如果是待支付订单，计算剩余时间并启动倒计时
    if (data.status === 'PENDING' && data.createTime) {
      const createTime = new Date(data.createTime).getTime();
      const now = Date.now();
      const elapsed = now - createTime;
      remainingTime.value = Math.max(0, ORDER_TIMEOUT - elapsed);
      
      // 启动倒计时
      startCountdown();
    }
  } else {
    showToast('加载订单详情失败');
    router.back();
  }
};

// 加载钱包信息
const loadWalletInfo = async () => {
  const data = await getWalletInfoSafe();
  if (data) {
    walletInfo.value = data;
  }
};

// 取消订单
const cancelOrder = () => {
  showConfirmDialog({
    title: '确认取消',
    message: '确定要取消该订单吗？',
  }).then(async () => {
    const result = await cancelOrderSafe(orderDetail.value.id);
    if (result !== null) {
      showToast('订单已取消');
      loadOrderDetail();
    }
  }).catch(() => {});
};

// 确认收货
const confirmReceipt = () => {
  showConfirmDialog({
    title: '确认收货',
    message: '确认已收到商品吗？',
  }).then(async () => {
    const result = await confirmReceiptSafe(orderDetail.value.id);
    if (result !== null) {
      showToast('确认收货成功');
      loadOrderDetail();
    }
  }).catch(() => {});
};

// 选择支付方式
const selectPaymentMethod = (method) => {
  if (method === 'WECHAT' || method === 'ALIPAY') {
    showToast('该支付方式暂不支持');
    return;
  }
  
  if (method === 'WALLET' && !isBalanceSufficient.value) {
    showToast('钱包余额不足，请先充值');
    return;
  }
  
  selectedPaymentMethod.value = method;
};

// 确认支付方式
const confirmPaymentMethod = () => {
  if (!selectedPaymentMethod.value) {
    showToast('请选择支付方式');
    return;
  }
  
  if (!isBalanceSufficient.value) {
    showToast('钱包余额不足，请先充值');
    return;
  }
  
  if (selectedPaymentMethod.value === 'WALLET') {
    // 钱包支付，显示密码输入框
    showPaymentMethodPopup.value = false;
    showPaymentPopup.value = true;
  } else {
    showToast('该支付方式暂不支持');
  }
};

// 处理按键
const handleKeyPress = (key) => {
  if (key === '') return;
  
  if (key === 'backspace') {
    password.value = password.value.slice(0, -1);
    passwordError.value = '';
  } else if (key === 'clear') {
    password.value = '';
    passwordError.value = '';
  } else {
    if (password.value.length < 6) {
      password.value += key;
      
      // 当密码达到6位时，验证密码
      if (password.value.length === 6) {
        payOrder();
      }
    }
  }
};

// 支付订单
const payOrder = async () => {
  try {
    const paymentData = {
      paymentMethod: selectedPaymentMethod.value,
      payPassword: password.value
    };
    
    const result = await payOrderSafe(orderDetail.value.id, paymentData);
    
    if (result !== null) {
      showPaymentPopup.value = false;
      password.value = '';
      passwordError.value = '';
      errorCount.value = 0;
      selectedPaymentMethod.value = '';
      
      showToast({
        message: '支付成功',
        icon: 'success'
      });
      
      setTimeout(() => {
        loadOrderDetail();
      }, 1500);
    }
  } catch (error) {
    // 判断是否是余额不足错误
    if (error.message && error.message.includes('余额不足')) {
      showPaymentPopup.value = false;
      password.value = '';
      passwordError.value = '';
      
      // 显示详细的余额不足信息
      showConfirmDialog({
        title: '余额不足',
        message: error.message,
        confirmButtonText: '去充值',
        cancelButtonText: '取消',
        showCancelButton: true,
      }).then(() => {
        // 跳转到钱包充值页面（如果有的话）
        showToast('充值功能开发中');
      }).catch(() => {});
    } else if (error.message || error.message.includes('密码错误')) {
      // 密码错误
      errorCount.value++;
      passwordError.value = error.message;
      password.value = '';
      
      if (errorCount.value >= 3) {
        showPaymentPopup.value = false;
        showToast('密码错误次数过多，钱包已锁定');
      }
    } else {
      // 其他错误
      passwordError.value = error.message || '支付失败';
      password.value = '';
      showPaymentPopup.value = false;
      showToast(error.message || '支付失败');
    }
  }
};

// 取消支付
const cancelPayment = () => {
  showConfirmDialog({
    title: '提示',
    message: '确定取消支付吗？',
  }).then(() => {
    showPaymentPopup.value = false;
    password.value = '';
    passwordError.value = '';
    errorCount.value = 0;
  }).catch(() => {});
};

// 启动倒计时
const startCountdown = () => {
  // 清除旧的定时器
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
  
  // 如果剩余时间小于等于0，不启动倒计时
  if (remainingTime.value <= 0) {
    return;
  }
  
  // 每秒更新一次倒计时
  countdownTimer = setInterval(() => {
    remainingTime.value -= 1000;
    
    // 倒计时结束
    if (remainingTime.value <= 0) {
      clearInterval(countdownTimer);
      showToast('订单已超时');
      // 刷新订单详情
      setTimeout(() => {
        loadOrderDetail();
      }, 1000);
    }
  }, 1000);
};

// 格式化倒计时
const formatCountdown = (milliseconds) => {
  if (milliseconds <= 0) {
    return '0:00';
  }
  const totalSeconds = Math.floor(milliseconds / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
};

// 返回
const goBack = () => {
  router.back();
};

// 跳转到商品详情
const goToGoodDetail = (commodityId) => {
  // 保存当前滚动位置
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
  router.push(`/good-details?id=${commodityId}`);
};

// 去评价
const goToComment = () => {
  saveScrollPosition(route.path, window.scrollY || window.pageYOffset);
  router.push(`/order/comment/${orderDetail.value.id}`);
};

onMounted(() => {
  loadOrderDetail();
  loadWalletInfo();
});

onUnmounted(() => {
  // 清除定时器
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
});
</script>

<style scoped>
.order-detail-view {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
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

/* 订单状态 */
.status-section {
  background-color: #fff;
  padding: 32px 16px;
  text-align: center;
  margin-bottom: 12px;
}

.status-icon {
  margin-bottom: 12px;
}

.status-text {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.status-tip {
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.status-tip .van-icon {
  font-size: 14px;
}

/* 收货地址 */
.address-section {
  background-color: #fff;
  padding: 16px;
  margin-bottom: 12px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.address-content {
  padding-left: 28px;
}

.receiver-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.receiver-phone {
  font-size: 14px;
  color: #666;
}

.address-detail {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

/* 商品信息 */
.goods-section {
  background-color: #fff;
  padding: 16px;
  margin-bottom: 12px;
}

.goods-list {
  padding-left: 28px;
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

/* 订单信息 */
.order-info-section {
  background-color: #fff;
  padding: 16px;
  margin-bottom: 12px;
}

:deep(.pay-amount-value) {
  color: #ff6b6b;
  font-weight: 600;
  font-size: 16px;
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.04);
  z-index: 99;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 支付密码弹窗 */
.payment-popup {
  width: 90vw;
  max-width: 400px;
  padding: 20px;
  background-color: #fff;
  border-radius: 12px;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.popup-title {
  font-size: 16px;
  font-weight: 600;
}

.wallet-balance {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin: 16px 0;
}

.pay-amount {
  text-align: center;
  margin: 16px 0;
}

.amount-label {
  font-size: 14px;
  color: #666;
  display: block;
  margin-bottom: 8px;
}

.amount-value {
  font-size: 32px;
  font-weight: 600;
  color: #ff6b6b;
}

.password-input {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin: 24px 0;
}

.password-box {
  width: 40px;
  height: 40px;
  border: 1px solid #ddd;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background-color: #fff;
}

.password-box.active {
  border-color: #07c160;
}

.dot {
  color: #333;
}

.password-error {
  text-align: center;
  color: #ff6b6b;
  font-size: 14px;
  margin-bottom: 12px;
}

.custom-keyboard {
  margin-top: 20px;
}

.keyboard-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.keyboard-key {
  flex: 1;
  height: 48px;
  background-color: #f5f5f5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 500;
  cursor: pointer;
  user-select: none;
  transition: all 0.2s;
}

.keyboard-key:active:not(.disabled) {
  background-color: #e0e0e0;
  transform: scale(0.95);
}

.keyboard-key.disabled {
  background-color: transparent;
  cursor: default;
}

.keyboard-key.clear {
  background-color: #ff6b6b;
  color: #fff;
}

.keyboard-key.backspace {
  background-color: #f5f5f5;
  color: #666;
}

/* 支付方式选择弹窗 */
.payment-method-popup {
  padding: 20px;
}

.payment-methods {
  margin: 20px 0;
}

.payment-method-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.payment-method-item.active {
  background-color: #e8f5e9;
  border: 1px solid #07c160;
}

.payment-method-item.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: #f5f5f5;
}

.payment-method-item.disabled .method-name {
  color: #999;
}

.method-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.method-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.method-tip {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

.wallet-balance {
  font-size: 14px;
  color: #07c160;
  margin-left: auto;
  font-weight: 500;
}

.wallet-balance.insufficient {
  color: #ff6b6b;
}

.payment-amount {
  text-align: center;
  margin: 20px 0;
  font-size: 16px;
  color: #333;
}

.payment-amount .amount {
  font-size: 24px;
  font-weight: 600;
  color: #ff6b6b;
  margin-left: 8px;
}
</style>
