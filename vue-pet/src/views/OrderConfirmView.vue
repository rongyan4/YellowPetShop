<template>
  <div class="order-confirm-view">
    <!-- 头部 -->
    <div class="header">
      <van-icon name="arrow-left" class="back-icon" @click="goBack" />
      <h1 class="title">确认订单</h1>
    </div>

    <div class="confirm-content">
      <!-- 收货地址 -->
      <div class="address-section" @click="selectAddress">
        <div class="section-title">
          <van-icon name="location-o" />
          <span>收货信息</span>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
        <div v-if="orderData.receiverName" class="address-content">
          <div class="receiver-info">
            <span class="receiver-name">{{ orderData.receiverName }}</span>
            <span class="receiver-phone">{{ orderData.receiverPhone }}</span>
          </div>
          <div class="address-detail">{{ orderData.receiverAddress }}</div>
        </div>
        <div v-else class="address-empty">
          <span>请选择收货地址</span>
        </div>
      </div>

      <!-- 商品信息 -->
      <div class="goods-section">
        <div class="section-title">
          <van-icon name="bag-o" />
          <span>商品信息</span>
        </div>
        <div class="goods-list">
          <div v-for="item in goodsList" :key="item.id" class="goods-item">
            <img :src="item.image" :alt="item.name" class="goods-image">
            <div class="goods-info">
              <div class="goods-name">{{ item.name }}</div>
              <div class="goods-bottom">
                <span class="goods-price">¥{{ item.price }}</span>
                <div class="quantity-control">
                  <button 
                    class="quantity-btn minus" 
                    @click="decreaseQuantity(item)"
                    :disabled="item.quantity <= 1"
                  >
                    <van-icon name="minus" />
                  </button>
                  <input 
                    type="number" 
                    class="quantity-input" 
                    v-model.number="item.quantity"
                    @blur="validateQuantity(item)"
                    min="1"
                  >
                  <button 
                    class="quantity-btn plus" 
                    @click="increaseQuantity(item)"
                  >
                    <van-icon name="plus" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 订单备注 -->
      <div class="remark-section">
        <van-field
          v-model="orderData.remark"
          label="订单备注"
          placeholder="选填，可以告诉卖家您的特殊需求"
          type="textarea"
          rows="2"
        />
      </div>

      <!-- 费用明细 -->
      <div class="price-section">
        <van-cell-group :border="false">
          <van-cell title="商品金额" :value="`¥${totalAmount}`" />
          <van-cell title="运费" :value="`¥${totalPostage}`" />
          <van-cell title="实付款" :value="`¥${payAmount}`" value-class="pay-amount-value" />
        </van-cell-group>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-bar">
      <div class="total-info">
        <span class="total-label">合计：</span>
        <span class="total-price">¥{{ payAmount }}</span>
      </div>
      <van-button type="primary" @click="submitOrder" :loading="submitting" :disabled="submitting">
        提交订单
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onActivated, onBeforeMount, onUnmounted } from 'vue';
import { useRouter, useRoute, onBeforeRouteEnter } from 'vue-router';
import { showToast } from 'vant';
import { getCartListSafe } from '@/api/cart';
import { getGoodDetailSafe } from '@/api/goods';
import { createOrderSafe } from '@/api/order';
import { getDefaultAddressSafe } from '@/api/address';

const router = useRouter();
const route = useRoute();

// 订单数据
const orderData = ref({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  remark: '',
  cartItemIds: []
});

// 商品列表
const goodsList = ref([]);

// 提交中
const submitting = ref(false);

// 请求标识，防止重复请求
let requestId = 0;

// 商品总金额
const totalAmount = computed(() => {
  return goodsList.value.reduce((sum, item) => {
    return sum + parseFloat(item.price) * item.quantity;
  }, 0).toFixed(2);
});

// 总运费
const totalPostage = computed(() => {
  return goodsList.value.reduce((sum, item) => {
    return sum + parseFloat(item.postage || 0);
  }, 0).toFixed(2);
});

// 实付金额
const payAmount = computed(() => {
  return (parseFloat(totalAmount.value) + parseFloat(totalPostage.value)).toFixed(2);
});

// 加载数据
const loadData = async () => {
  const { cartItemIds, from } = route.query;
  
  console.log('=== 订单确认页面 - 开始加载数据 ===');
  console.log('1. 路由参数 cartItemIds:', cartItemIds);
  console.log('2. 来源标识 from:', from);
  
  if (!cartItemIds) {
    console.error('错误: cartItemIds 参数不存在');
    showToast('订单数据错误');
    router.back();
    return;
  }
  
  try {
    const ids = JSON.parse(cartItemIds);
    console.log('3. 解析后的 IDs:', ids);
    console.log('   - IDs 类型:', typeof ids);
    console.log('   - IDs 是否为数组:', Array.isArray(ids));
    console.log('   - IDs 长度:', ids.length);
    
    orderData.value.cartItemIds = ids;
    
    // 判断是从购物车来还是立即购买
    if (from === 'cart') {
      // 从购物车下单：ids 是购物车项ID
      console.log('4. 从购物车下单，获取购物车数据...');
      const cartData = await getCartListSafe();
      console.log('5. 购物车数据返回:', cartData);
      
      if (cartData && Array.isArray(cartData)) {
        console.log('   - 购物车商品数量:', cartData.length);
        
        const filteredItems = cartData.filter(item => ids.includes(item.id));
        console.log('6. 过滤后的商品数量:', filteredItems.length);
        
        goodsList.value = filteredItems.map(item => ({
          id: item.id,
          commodityId: item.commodityId,
          name: item.name,
          price: parseFloat(item.price),
          quantity: item.quantity,
          image: item.mainPicUrl || '/images/goods/ml.png',
          postage: parseFloat(item.postage || 0)
        }));
      }
    } else {
      // 立即购买：ids 是商品ID
      console.log('4. 立即购买，直接获取商品详情...');
      const goodsPromises = ids.map(id => getGoodDetailSafe(id));
      const goodsDetails = await Promise.all(goodsPromises);
      
      console.log('5. 商品详情返回:', goodsDetails);
      
      goodsList.value = goodsDetails
        .filter(item => item !== null)
        .map(item => {
          const priceStr = item.price.toString();
          const [intPart, decimalPart = '00'] = priceStr.split('.');
          const fullPrice = `${intPart}.${decimalPart.padEnd(2, '0')}`;
          
          return {
            id: item.id,
            commodityId: item.id,
            name: `${item.name} ${item.unit || ''}`,
            price: parseFloat(fullPrice),
            quantity: 1, // 立即购买默认数量为1
            image: item.mainPicUrl || '/images/goods/ml.png',
            postage: parseFloat(item.postage || 0)
          };
        });
    }
    
    console.log('7. 最终商品列表:', goodsList.value);
    console.log('=== 数据加载完成 ===');
    
    if (goodsList.value.length === 0) {
      console.error('错误: 没有找到任何商品');
      showToast('商品信息加载失败');
      router.back();
    }
  } catch (error) {
    console.error('加载数据失败:', error);
    console.error('错误堆栈:', error.stack);
    showToast('数据加载失败');
    router.back();
  }
};

// 增加数量
const increaseQuantity = (item) => {
  item.quantity++;
};

// 减少数量
const decreaseQuantity = (item) => {
  if (item.quantity > 1) {
    item.quantity--;
  }
};

// 验证数量
const validateQuantity = (item) => {
  if (!item.quantity || item.quantity < 1) {
    item.quantity = 1;
  }
  item.quantity = Math.floor(item.quantity);
};

// 提交订单
const submitOrder = async () => {
  console.log('=== 开始提交订单 ===');
  
  // 验证收货信息
  if (!orderData.value.receiverName) {
    showToast('请输入收货人姓名');
    return;
  }
  if (!orderData.value.receiverPhone) {
    showToast('请输入联系电话');
    return;
  }
  if (!orderData.value.receiverAddress) {
    showToast('请输入收货地址');
    return;
  }
  
  // 验证手机号格式
  const phoneReg = /^1[3-9]\d{9}$/;
  if (!phoneReg.test(orderData.value.receiverPhone)) {
    showToast('请输入正确的手机号');
    return;
  }
  
  // 防止重复提交
  if (submitting.value) {
    console.warn('订单正在提交中，请勿重复点击');
    return;
  }
  
  submitting.value = true;
  const currentRequestId = ++requestId;
  const requestTimestamp = Date.now();
  console.log('当前请求ID:', currentRequestId);
  console.log('请求时间戳:', requestTimestamp);
  console.log('请求开始时间:', new Date().toISOString());
  
  try {
    // 构建订单数据
    const orderRequest = {
      receiverName: orderData.value.receiverName,
      receiverPhone: orderData.value.receiverPhone,
      receiverAddress: orderData.value.receiverAddress,
      remark: orderData.value.remark,
      items: goodsList.value.map(item => ({
        commodityId: item.commodityId,
        quantity: item.quantity,
        cartItemId: item.id // 如果是从购物车来的，会有购物车项ID
      }))
    };
    
    console.log('1. 提交订单数据:', JSON.stringify(orderRequest, null, 2));
    console.log('2. items 数组长度:', orderRequest.items.length);
    console.log('3. items 详情:', orderRequest.items);
    console.log('4. 即将发送请求...');
    
    const result = await createOrderSafe(orderRequest);
    
    console.log('5. 请求完成时间:', new Date().toISOString());
    console.log('6. 请求耗时:', Date.now() - requestTimestamp, 'ms');
    
    // 检查是否是最新的请求
    if (currentRequestId !== requestId) {
      console.warn('检测到新的请求，忽略旧请求的响应');
      return;
    }
    
    console.log('7. 订单创建结果:', result);
    
    if (result) {
      // 直接跳转到订单详情页，无需提示
      router.replace(`/order/detail/${result.id}`);
    } else {
      console.error('8. 订单创建失败: result 为 null');
      showToast('订单创建失败');
      submitting.value = false;
    }
  } catch (error) {
    console.error('9. 创建订单异常:', error);
    console.error('   异常时间:', new Date().toISOString());
    console.error('   错误消息:', error.message);
    console.error('   错误堆栈:', error.stack);
    showToast(error.message || '订单创建失败');
    submitting.value = false;
  }
  
  console.log('=== 订单提交流程结束 ===');
  console.log('结束时间:', new Date().toISOString());
};

// 加载默认地址
const loadDefaultAddress = async () => {
  const address = await getDefaultAddressSafe();
  if (address) {
    orderData.value.receiverName = address.receiverName;
    orderData.value.receiverPhone = address.receiverPhone;
    orderData.value.receiverAddress = `${address.province} ${address.city} ${address.district} ${address.detailAddress}`;
  }
};

// 选择地址
const selectAddress = () => {
  router.push('/address/list');
};

// 检查是否有选中的地址（从地址列表返回）
const checkSelectedAddress = () => {
  console.log('=== 检查选中的地址 ===');
  const selectedAddress = localStorage.getItem('selectedAddress');
  console.log('1. localStorage 中的地址数据:', selectedAddress);
  
  if (selectedAddress) {
    try {
      const address = JSON.parse(selectedAddress);
      console.log('2. 解析后的地址对象:', address);
      console.log('3. 地址字段 - name:', address.name);
      console.log('4. 地址字段 - tel:', address.tel);
      console.log('5. 地址字段 - address:', address.address);
      
      orderData.value.receiverName = address.name;
      orderData.value.receiverPhone = address.tel;
      orderData.value.receiverAddress = address.address;
      
      console.log('6. 更新后的 orderData:', orderData.value);
      console.log('=== 地址更新成功 ===');
      
      // 清除缓存
      localStorage.removeItem('selectedAddress');
    } catch (error) {
      console.error('解析地址失败:', error);
    }
  } else {
    console.log('localStorage 中没有选中的地址');
  }
};

// 返回
const goBack = () => {
  router.back();
};

// 监听页面可见性变化（当从地址列表返回时触发）
const handleVisibilityChange = () => {
  if (!document.hidden) {
    console.log('=== 页面变为可见，检查地址 ===');
    checkSelectedAddress();
  }
};

onBeforeMount(() => {
  // 在组件挂载前检查选中的地址
  checkSelectedAddress();
});

onMounted(() => {
  loadData();
  // 如果没有选中地址，则加载默认地址
  if (!orderData.value.receiverName) {
    loadDefaultAddress();
  }
  
  // 监听页面可见性变化
  document.addEventListener('visibilitychange', handleVisibilityChange);
  
  // 监听 storage 事件（跨标签页通信）
  window.addEventListener('storage', checkSelectedAddress);
  
  // 监听自定义事件（同页面内通信）
  window.addEventListener('addressSelected', checkSelectedAddress);
});

onUnmounted(() => {
  // 清理事件监听
  document.removeEventListener('visibilitychange', handleVisibilityChange);
  window.removeEventListener('storage', checkSelectedAddress);
  window.removeEventListener('addressSelected', checkSelectedAddress);
});

// 页面激活时检查是否有选中的地址（用于 keep-alive 缓存的情况）
onActivated(() => {
  checkSelectedAddress();
});
</script>

<style scoped>
.order-confirm-view {
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

/* 内容区域 */
.confirm-content {
  padding-bottom: 20px;
}

/* 收货地址 */
.address-section {
  background-color: #fff;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.address-section:active {
  background-color: #f5f5f5;
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

.arrow-icon {
  margin-left: auto;
  color: #999;
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

.address-empty {
  padding-left: 28px;
  font-size: 14px;
  color: #999;
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

/* 订单备注 */
.remark-section {
  background-color: #fff;
  margin-bottom: 12px;
}

/* 费用明细 */
.price-section {
  background-color: #fff;
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
  justify-content: space-between;
  align-items: center;
}

.total-info {
  display: flex;
  align-items: baseline;
}

.total-label {
  font-size: 14px;
  color: #666;
}

.total-price {
  font-size: 22px;
  color: #ff6b6b;
  font-weight: 600;
  font-family: 'DIN Alternate', 'Arial', sans-serif;
}
</style>
