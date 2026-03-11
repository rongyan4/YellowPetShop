<template>
  <div class="car-view">
    <!-- 头部 -->
    <div class="header">
      <h1 class="title">购物车</h1>
      <div class="edit-btn" @click="toggleEditMode" v-if="cartItems.length > 0">
        {{ isEditMode ? '完成' : '编辑' }}
      </div>
    </div>

    <!-- 购物车内容 -->
    <div class="cart-content" v-if="isLoggedIn">
      <!-- 有商品 -->
      <div v-if="cartItems.length > 0" class="cart-list">
        <div 
          v-for="item in cartItems" 
          :key="item.id" 
          class="cart-item"
          :class="{ 'edit-mode': isEditMode }"
        >
          <!-- 选择框 -->
          <div class="checkbox" @click="toggleSelect(item.id)">
            <div class="checkbox-inner" :class="{ checked: item.selected }">
              <svg v-if="item.selected" viewBox="0 0 24 24" width="16" height="16">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" fill="white"/>
              </svg>
            </div>
          </div>

          <!-- 商品图片 -->
          <div class="item-image" @click="goToGoodDetail(item.commodityId)">
            <img :src="item.image" :alt="item.name">
          </div>

          <!-- 商品信息 -->
          <div class="item-info">
            <div class="item-name" @click="goToGoodDetail(item.commodityId)">{{ item.name }}</div>
            <div class="item-spec">{{ item.spec }}</div>
            <div class="item-bottom">
              <div class="item-price">
                <span class="currency">¥</span>
                <span class="price">{{ item.price }}</span>
              </div>
              
              <!-- 数量控制 -->
              <div class="quantity-control" v-if="!isEditMode">
                <button 
                  class="quantity-btn minus" 
                  @click="decreaseQuantity(item.id)"
                  :disabled="item.quantity <= 1"
                >
                  <svg viewBox="0 0 24 24" width="14" height="14">
                    <path d="M19 13H5v-2h14v2z" fill="currentColor"/>
                  </svg>
                </button>
                <input 
                  type="text" 
                  class="quantity-input" 
                  :value="item.quantity"
                  readonly
                >
                <button 
                  class="quantity-btn plus" 
                  @click="increaseQuantity(item.id)"
                >
                  <svg viewBox="0 0 24 24" width="14" height="14">
                    <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空购物车 -->
      <div v-else class="empty-cart">
        <div class="empty-icon">
          <svg viewBox="0 0 120 120" width="120" height="120">
            <circle cx="60" cy="60" r="50" fill="#f5f5f5" stroke="#e0e0e0" stroke-width="2"/>
            <path d="M40 45 L45 70 L75 70 L80 45 Z" fill="#ccc" stroke="#999" stroke-width="2"/>
            <circle cx="50" cy="80" r="4" fill="#999"/>
            <circle cx="70" cy="80" r="4" fill="#999"/>
            <line x1="35" y1="35" x2="45" y2="45" stroke="#999" stroke-width="2"/>
          </svg>
        </div>
        <p class="empty-text">购物车空空如也</p>
        <p class="empty-subtext">快去挑选心仪的商品吧~</p>
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
        <p class="login-text">登录后查看购物车</p>
        <button class="login-btn" @click="showLoginModal">立即登录</button>
      </div>
    </div>

    <!-- 底部结算栏 -->
    <div class="bottom-bar" v-if="isLoggedIn && cartItems.length > 0">
      <div class="select-all" @click="toggleSelectAll">
        <div class="checkbox-inner" :class="{ checked: isAllSelected }">
          <svg v-if="isAllSelected" viewBox="0 0 24 24" width="16" height="16">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" fill="white"/>
          </svg>
        </div>
        <span class="select-all-text">全选</span>
      </div>

      <div class="bottom-right">
        <div class="total-section" v-if="!isEditMode">
          <span class="total-label">合计：</span>
          <span class="total-price">
            <span class="currency">¥</span>
            <span class="price">{{ totalPrice }}</span>
          </span>
        </div>
        
        <button 
          class="checkout-btn" 
          :class="{ disabled: selectedCount === 0 }"
          @click="handleCheckout"
        >
          {{ isEditMode ? `删除(${selectedCount})` : `结算(${selectedCount})` }}
        </button>
      </div>
    </div>

    <!-- TabBar -->
    <TabBar />

    <!-- 登录弹窗 -->
    <LoginChoice v-if="showLoginChoice" @close="closeLoginModal" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';
import TabBar from '@/components/TabBar.vue';
import LoginChoice from '@/components/login/LoginChoice.vue';
import { getCartListSafe, updateCartQuantitySafe, updateCartCheckedSafe, deleteCartItemSafe } from '@/api/cart';
import { showToast, showDialog } from 'vant';

const userStore = useUserStore();
const router = useRouter();

// 登录状态
const isLoggedIn = computed(() => userStore.isLoggedIn);
const showLoginChoice = ref(false);

// 编辑模式
const isEditMode = ref(false);

// 购物车商品数据
const cartItems = ref([]);
const loading = ref(false);

// 加载购物车数据
const loadCartData = async () => {
  if (!isLoggedIn.value) {
    cartItems.value = [];
    return;
  }
  
  try {
    loading.value = true;
    const data = await getCartListSafe();
    
    if (data) {
      // 转换数据格式
      cartItems.value = data.map(item => ({
        id: item.id,
        commodityId: item.commodityId,
        name: item.name,
        spec: `规格: ${item.unit || '标准装'}`,
        price: parseFloat(item.price),
        quantity: item.quantity,
        image: item.mainPicUrl || '/images/goods/ml.png',
        selected: item.checked || false,
        isValid: item.isValid !== false
      }));
      
      console.log('购物车数据加载成功:', cartItems.value);
    }
  } catch (error) {
    console.error('加载购物车失败:', error);
    showToast('加载购物车失败');
  } finally {
    loading.value = false;
  }
};

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value;
};

// 切换单个商品选中状态
const toggleSelect = async (id) => {
  const item = cartItems.value.find(item => item.id === id);
  if (item) {
    const newChecked = !item.selected;
    item.selected = newChecked;
    
    // 更新到后端
    const success = await updateCartCheckedSafe(id, newChecked);
    if (!success) {
      // 如果更新失败，恢复状态
      item.selected = !newChecked;
      showToast('更新失败');
    }
  }
};

// 是否全选
const isAllSelected = computed(() => {
  return cartItems.value.length > 0 && cartItems.value.every(item => item.selected);
});

// 切换全选
const toggleSelectAll = async () => {
  const newState = !isAllSelected.value;
  
  // 批量更新
  const updatePromises = cartItems.value.map(item => {
    item.selected = newState;
    return updateCartCheckedSafe(item.id, newState);
  });
  
  try {
    await Promise.all(updatePromises);
  } catch (error) {
    console.error('批量更新失败:', error);
    showToast('更新失败');
    // 重新加载数据
    loadCartData();
  }
};

// 已选商品数量
const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length;
});

// 总价
const totalPrice = computed(() => {
  const total = cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.price * item.quantity, 0);
  return total.toFixed(2);
});

// 增加数量
const increaseQuantity = async (id) => {
  const item = cartItems.value.find(item => item.id === id);
  if (item) {
    const newQuantity = item.quantity + 1;
    item.quantity = newQuantity;
    
    // 更新到后端
    const success = await updateCartQuantitySafe(id, newQuantity);
    if (!success) {
      // 如果更新失败，恢复数量
      item.quantity = newQuantity - 1;
      showToast('更新失败');
    }
  }
};

// 减少数量
const decreaseQuantity = async (id) => {
  const item = cartItems.value.find(item => item.id === id);
  if (item && item.quantity > 1) {
    const newQuantity = item.quantity - 1;
    item.quantity = newQuantity;
    
    // 更新到后端
    const success = await updateCartQuantitySafe(id, newQuantity);
    if (!success) {
      // 如果更新失败，恢复数量
      item.quantity = newQuantity + 1;
      showToast('更新失败');
    }
  }
};

// 结算或删除
const handleCheckout = async () => {
  if (selectedCount.value === 0) return;
  
  if (isEditMode.value) {
    // 删除选中的商品
    const selectedItems = cartItems.value.filter(item => item.selected);
    const deletePromises = selectedItems.map(item => deleteCartItemSafe(item.id));
    
    try {
      await Promise.all(deletePromises);
      // 从列表中移除
      cartItems.value = cartItems.value.filter(item => !item.selected);
      isEditMode.value = false;
      showToast('删除成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToast('删除失败');
    }
  } else {
    // 结算 - 跳转到订单确认页
    const selectedItems = cartItems.value.filter(item => item.selected);
    
    if (selectedItems.length === 0) {
      showToast('请选择要结算的商品');
      return;
    }
    
    // 跳转到订单确认页，统一使用 cartItemIds
    const cartItemIds = selectedItems.map(item => item.id);
    router.push({
      path: '/order/confirm',
      query: {
        cartItemIds: JSON.stringify(cartItemIds),
        from: 'cart' // 标识来自购物车
      }
    });
  }
};

// 去购物
const goShopping = () => {
  router.push('/shopping');
};

// 跳转到商品详情页
const goToGoodDetail = (commodityId) => {
  router.push(`/good-details?id=${commodityId}`);
};

// 显示登录弹窗
const showLoginModal = () => {
  showLoginChoice.value = true;
};

// 关闭登录弹窗
const closeLoginModal = () => {
  showLoginChoice.value = false;
};

// 监听登录状态变化
watch(() => userStore.isLoggedIn, (newVal) => {
  if (newVal) {
    // 登录成功后关闭弹窗并加载购物车数据
    showLoginChoice.value = false;
    loadCartData();
  } else {
    // 退出登录时清空购物车
    cartItems.value = [];
  }
});

onMounted(() => {
  // 检查用户是否登录
  if (userStore.isLoggedIn) {
    // 已登录，加载购物车数据
    loadCartData();
  } else {
    // 未登录时清空购物车数据
    cartItems.value = [];
  }
});
</script>

<style scoped>
.car-view {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 1.6rem;
  display: flex;
  flex-direction: column;
}

/* 头部 */
.header {
  position: sticky;
  top: 0;
  background-color: #fff;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.edit-btn {
  font-size: 15px;
  color: #666;
  cursor: pointer;
  padding: 4px 12px;
  transition: color 0.2s;
}

.edit-btn:active {
  color: #333;
}

/* 购物车内容 */
.cart-content {
  flex: 1;
  overflow-y: auto;
}

/* 购物车列表 */
.cart-list {
  padding: 12px 0;
}

.cart-item {
  background-color: #fff;
  margin: 0 12px 12px;
  padding: 16px;
  border-radius: 12px;
  display: flex;
  gap: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.cart-item:active {
  transform: scale(0.98);
}

/* 选择框 */
.checkbox {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px;
}

.checkbox-inner {
  width: 20px;
  height: 20px;
  border: 2px solid #ddd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  background-color: #fff;
}

.checkbox-inner.checked {
  background-color: #FFD700;
  border-color: #FFD700;
}

/* 商品图片 */
.item-image {
  width: 90px;
  height: 90px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background-color: #f5f5f5;
  cursor: pointer;
  transition: opacity 0.2s;
}

.item-image:active {
  opacity: 0.7;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息 */
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.item-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  transition: color 0.2s;
}

.item-name:active {
  color: #7cb342;
}

.item-spec {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.item-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  display: flex;
  align-items: baseline;
}

.currency {
  font-size: 14px;
  color: #ff6b6b;
  font-weight: 500;
}

.price {
  font-size: 20px;
  color: #ff6b6b;
  font-weight: 600;
  font-family: 'DIN Alternate', 'Arial', sans-serif;
}

/* 数量控制 */
.quantity-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity-btn {
  width: 24px;
  height: 24px;
  border: 1px solid #ddd;
  border-radius: 50%;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: #666;
}

.quantity-btn:active:not(:disabled) {
  background-color: #f5f5f5;
  transform: scale(0.9);
}

.quantity-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.quantity-input {
  width: 40px;
  height: 24px;
  text-align: center;
  border: none;
  font-size: 14px;
  color: #333;
  font-weight: 500;
  background-color: transparent;
}

/* 空购物车 */
.empty-cart {
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
  background: linear-gradient(135deg, #FFE033 0%, #FFD700 100%);
  color: #333;
  border: none;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(255, 215, 0, 0.4);
}

.go-shopping-btn:active {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(255, 215, 0, 0.4);
}

/* 未登录状态 */
.not-logged-in {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
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
  background: linear-gradient(135deg, #FFE033 0%, #FFD700 100%);
  color: #333;
  border: none;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(255, 215, 0, 0.4);
}

.login-btn:active {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(255, 215, 0, 0.4);
}

/* 底部结算栏 */
.bottom-bar {
  position: fixed;
  bottom: 1.6rem;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #f0f0f0;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.04);
  z-index: 99;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.select-all-text {
  font-size: 14px;
  color: #333;
}

.bottom-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.total-section {
  display: flex;
  align-items: baseline;
}

.total-label {
  font-size: 14px;
  color: #666;
}

.total-price {
  display: flex;
  align-items: baseline;
}

.total-price .currency {
  font-size: 14px;
  color: #ff6b6b;
  font-weight: 500;
}

.total-price .price {
  font-size: 22px;
  color: #ff6b6b;
  font-weight: 600;
  font-family: 'DIN Alternate', 'Arial', sans-serif;
}

.checkout-btn {
  padding: 10px 28px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff5252 100%);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
  white-space: nowrap;
}

.checkout-btn:active:not(.disabled) {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.checkout-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .item-image {
    width: 80px;
    height: 80px;
  }
  
  .item-name {
    font-size: 14px;
  }
  
  .price {
    font-size: 18px;
  }
}
</style>
