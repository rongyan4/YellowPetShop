<template>
  <div class="order-detail">
    <div class="detail-card">
      <div class="card-header">
        <h3>订单信息</h3>
        <span class="status-badge" :class="getStatusClass(orderDetail.status)">
          {{ getStatusText(orderDetail.status) }}
        </span>
      </div>
      
      <div class="info-section">
        <div class="info-row">
          <span class="label">订单号：</span>
          <span class="value">{{ orderDetail.orderSn }}</span>
        </div>
        <div class="info-row">
          <span class="label">创建时间：</span>
          <span class="value">{{ formatDate(orderDetail.createTime) }}</span>
        </div>
        <div class="info-row" v-if="orderDetail.payTime">
          <span class="label">支付时间：</span>
          <span class="value">{{ formatDate(orderDetail.payTime) }}</span>
        </div>
        <div class="info-row" v-if="orderDetail.shipTime">
          <span class="label">发货时间：</span>
          <span class="value">{{ formatDate(orderDetail.shipTime) }}</span>
        </div>
      </div>
    </div>

    <div class="detail-card">
      <div class="card-header">
        <h3>收货信息</h3>
      </div>
      
      <div class="info-section">
        <div class="info-row">
          <span class="label">收货人：</span>
          <span class="value">{{ orderDetail.receiverName }}</span>
        </div>
        <div class="info-row">
          <span class="label">联系电话：</span>
          <span class="value">{{ orderDetail.receiverPhone }}</span>
        </div>
        <div class="info-row">
          <span class="label">收货地址：</span>
          <span class="value">{{ orderDetail.receiverAddress }}</span>
        </div>
      </div>
    </div>

    <div class="detail-card">
      <div class="card-header">
        <h3>商品信息</h3>
      </div>
      
      <div class="goods-list">
        <div class="goods-item" v-for="item in orderDetail.orderItems" :key="item.id">
          <img :src="item.commodityPic || '/images/goods/ml.png'" :alt="item.commodityName" class="goods-image" />
          <div class="goods-info">
            <div class="goods-name">{{ item.commodityName }}</div>
            <div class="goods-spec">数量：{{ item.quantity }}</div>
          </div>
          <div class="goods-price">¥{{ parseFloat(item.commodityPrice || 0).toFixed(2) }}</div>
          <div class="goods-quantity">x{{ item.quantity }}</div>
          <div class="goods-total">¥{{ parseFloat(item.totalPrice || 0).toFixed(2) }}</div>
        </div>
      </div>
    </div>

    <div class="detail-card">
      <div class="card-header">
        <h3>费用信息</h3>
      </div>
      
      <div class="info-section">
        <div class="info-row">
          <span class="label">商品总额：</span>
          <span class="value">¥{{ orderDetail.totalAmount }}</span>
        </div>
        <div class="info-row" v-if="orderDetail.originalAmount && orderDetail.priceModified">
          <span class="label">原价：</span>
          <span class="value" style="text-decoration: line-through; color: #999;">¥{{ orderDetail.originalAmount }}</span>
        </div>
        <div class="info-row">
          <span class="label">运费：</span>
          <span class="value">¥{{ orderDetail.postage || 0 }}</span>
        </div>
        <div class="info-row total-row">
          <span class="label">实付金额：</span>
          <span class="value price-highlight">¥{{ orderDetail.payAmount }}</span>
        </div>
      </div>
    </div>

    <div class="detail-card" v-if="orderDetail.status === 'SHIPPED' || orderDetail.status === 'COMPLETED' || logisticsList.length > 0">
      <div class="card-header">
        <h3>物流信息</h3>
        <van-button size="small" type="primary" @click="handleAddLogistics" v-if="orderDetail.status === 'SHIPPED' || orderDetail.status === 'COMPLETED'">添加物流</van-button>
      </div>
      
      <div class="logistics-list" v-if="logisticsList.length > 0">
        <div class="logistics-item" v-for="item in logisticsList" :key="item.id">
          <div class="logistics-info">
            <div class="logistics-row">
              <span class="logistics-label">物流公司：</span>
              <span class="logistics-value">{{ item.shippingCompany }}</span>
            </div>
            <div class="logistics-row">
              <span class="logistics-label">物流单号：</span>
              <span class="logistics-value">{{ item.trackingNo }}</span>
            </div>
            <div class="logistics-row" v-if="item.remark">
              <span class="logistics-label">备注：</span>
              <span class="logistics-value">{{ item.remark }}</span>
            </div>
            <div class="logistics-row">
              <span class="logistics-label">发货时间：</span>
              <span class="logistics-value">{{ formatDate(item.shippingTime) }}</span>
            </div>
          </div>
          <div class="logistics-actions">
            <van-button size="small" @click="handleEditLogistics(item)">编辑</van-button>
            <van-button size="small" type="danger" @click="handleDeleteLogistics(item.id)">删除</van-button>
          </div>
        </div>
      </div>
      
      <div class="empty-logistics" v-else>
        <p>暂无物流信息，请点击上方"添加物流"按钮添加</p>
      </div>
    </div>

    <div class="detail-card" v-if="orderDetail.remark">
      <div class="card-header">
        <h3>备注信息</h3>
      </div>
      
      <div class="info-section">
        <div class="remark-content">{{ orderDetail.remark }}</div>
      </div>
    </div>

    <div class="action-bar">
      <van-button 
        v-if="orderDetail.status === 'PENDING'" 
        type="warning" 
        @click="handleUpdatePrice"
      >
        修改价格
      </van-button>
      <van-button 
        v-if="orderDetail.status === 'PAID'" 
        type="primary" 
        @click="handleShip"
      >
        发货
      </van-button>
      <van-button 
        v-if="orderDetail.status === 'SHIPPED'" 
        type="success" 
        @click="handleComplete"
      >
        已送达
      </van-button>
      <van-button @click="goBack">返回</van-button>
    </div>

    <!-- 改价对话框 -->
    <van-popup v-model:show="showPriceDialog" position="center" :style="{ width: '90%', maxWidth: '400px', borderRadius: '16px' }">
      <div class="dialog">
        <div class="dialog-header">
          <h3>修改订单价格</h3>
          <button @click="showPriceDialog = false" class="close-btn">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>原商品金额</label>
            <input type="text" :value="'¥' + orderDetail.totalAmount" disabled />
          </div>
          <div class="form-group">
            <label>新商品金额</label>
            <input v-model="newTotalAmount" type="number" step="0.01" placeholder="请输入新商品金额" />
          </div>
          <div class="form-group">
            <label>邮费</label>
            <input v-model="newPostage" type="number" step="0.01" placeholder="请输入邮费" />
          </div>
          <div class="form-group">
            <label>改价原因</label>
            <textarea v-model="priceReason" placeholder="请输入改价原因（可选）" rows="2"></textarea>
          </div>
        </div>
        <div class="dialog-footer">
          <button @click="showPriceDialog = false" class="btn-cancel">取消</button>
          <button @click="confirmUpdatePrice" class="btn-confirm">确定</button>
        </div>
      </div>
    </van-popup>

    <!-- 发货对话框 -->
    <van-popup v-model:show="showShipDialog" position="center" :style="{ width: '90%', maxWidth: '400px', borderRadius: '16px' }">
      <div class="dialog">
        <div class="dialog-header">
          <h3>订单发货</h3>
          <button @click="showShipDialog = false" class="close-btn">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>物流公司</label>
            <select v-model="shippingCompany" class="form-select">
              <option value="顺丰速运">顺丰速运</option>
              <option value="中通快递">中通快递</option>
              <option value="圆通速递">圆通速递</option>
              <option value="申通快递">申通快递</option>
              <option value="韵达快递">韵达快递</option>
              <option value="邮政EMS">邮政EMS</option>
              <option value="京东物流">京东物流</option>
            </select>
          </div>
          <div class="form-group">
            <label>物流单号</label>
            <input v-model="trackingNo" type="text" placeholder="请输入物流单号" />
          </div>
          <div class="form-group">
            <label>发货备注</label>
            <textarea v-model="shipRemark" placeholder="请输入发货备注（可选）" rows="2"></textarea>
          </div>
        </div>
        <div class="dialog-footer">
          <button @click="showShipDialog = false" class="btn-cancel">取消</button>
          <button @click="confirmShip" class="btn-confirm">确定</button>
        </div>
      </div>
    </van-popup>
    
    <!-- 物流信息对话框 -->
    <van-popup v-model:show="showLogisticsDialog" position="center" :style="{ width: '90%', maxWidth: '400px', borderRadius: '16px' }">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ currentLogistics ? '编辑物流信息' : '添加物流信息' }}</h3>
          <button @click="showLogisticsDialog = false" class="close-btn">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>物流公司</label>
            <select v-model="logisticsForm.shippingCompany" class="form-select">
              <option value="顺丰速运">顺丰速运</option>
              <option value="中通快递">中通快递</option>
              <option value="圆通速递">圆通速递</option>
              <option value="申通快递">申通快递</option>
              <option value="韵达快递">韵达快递</option>
              <option value="邮政EMS">邮政EMS</option>
              <option value="京东物流">京东物流</option>
            </select>
          </div>
          <div class="form-group">
            <label>物流单号</label>
            <input v-model="logisticsForm.trackingNo" type="text" placeholder="请输入物流单号" />
          </div>
          <div class="form-group">
            <label>备注</label>
            <textarea v-model="logisticsForm.remark" placeholder="请输入备注（可选）" rows="2"></textarea>
          </div>
        </div>
        <div class="dialog-footer">
          <button @click="showLogisticsDialog = false" class="btn-cancel">取消</button>
          <button @click="confirmLogistics" class="btn-confirm">确定</button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getOrderDetail, updateOrderPrice, shipOrder, completeOrder, getOrderLogistics, addLogistics, updateLogistics, deleteLogistics } from '@/api/merchant';
import { showSuccessToast, showFailToast, showToast, showConfirmDialog } from 'vant';
const router = useRouter();
const route = useRoute();

const orderDetail = ref({
  orderItems: []
});

const logisticsList = ref([]);
const showPriceDialog = ref(false);
const showShipDialog = ref(false);
const showLogisticsDialog = ref(false);
const currentLogistics = ref(null);

const newTotalAmount = ref('');
const newPostage = ref('');
const priceReason = ref('');
const trackingNo = ref('');
const shippingCompany = ref('顺丰速运');
const shipRemark = ref('');

const logisticsForm = ref({
  shippingCompany: '顺丰速运',
  trackingNo: '',
  remark: ''
});

const loadOrderDetail = async () => {
  try {
    const orderId = route.params.id;
    const result = await getOrderDetail(orderId);
    
    if (result && result.code === 200) {
      orderDetail.value = result.data;
      loadLogisticsList();
    } else {
      showFailToast('加载订单详情失败');
    }
  } catch (error) {
    console.error('加载订单详情失败:', error);
    showFailToast('加载失败');
  }
};

const loadLogisticsList = async () => {
  try {
    const result = await getOrderLogistics(orderDetail.value.id);
    if (result && result.code === 200) {
      logisticsList.value = result.data || [];
    }
  } catch (error) {
    console.error('加载物流信息失败:', error);
    logisticsList.value = [];
  }
};

const handleUpdatePrice = () => {
  newTotalAmount.value = orderDetail.value.totalAmount;
  newPostage.value = orderDetail.value.postage || 0;
  priceReason.value = '';
  showPriceDialog.value = true;
};

const confirmUpdatePrice = async () => {
  if (!newTotalAmount.value || newTotalAmount.value <= 0) {
    showToast('请输入有效的价格');
    return;
  }

  try {
    const result = await updateOrderPrice({
      orderId: orderDetail.value.id,
      newTotalAmount: parseFloat(newTotalAmount.value),
      newPostage: parseFloat(newPostage.value),
      reason: priceReason.value
    });

    if (result && result.code === 200) {
      showSuccessToast('价格修改成功');
      showPriceDialog.value = false;
      loadOrderDetail();
    } else {
      showFailToast(result.msg || '修改失败');
    }
  } catch (error) {
    showFailToast('修改失败');
  }
};

const handleShip = () => {
  trackingNo.value = '';
  shippingCompany.value = '顺丰速运';
  shipRemark.value = '';
  showShipDialog.value = true;
};

const confirmShip = async () => {
  if (!trackingNo.value.trim()) {
    showToast('请输入物流单号');
    return;
  }

  try {
    // 1. 更新订单状态为已发货
    const shipResult = await shipOrder({
      orderId: orderDetail.value.id,
      shippingCompany: shippingCompany.value,
      trackingNo: trackingNo.value,
      remark: shipRemark.value
    });

    if (shipResult && shipResult.code === 200) {
      // 2. 同时创建物流记录
      const logisticsResult = await addLogistics({
        orderId: orderDetail.value.id,
        shippingCompany: shippingCompany.value,
        trackingNo: trackingNo.value,
        remark: shipRemark.value
      });

      if (logisticsResult && logisticsResult.code === 200) {
        showSuccessToast('发货成功');
        showShipDialog.value = false;
        loadOrderDetail();
      } else {
        showSuccessToast('发货成功，但物流记录创建失败，请手动添加');
        showShipDialog.value = false;
        loadOrderDetail();
      }
    } else {
      showFailToast(shipResult.msg || '发货失败');
    }
  } catch (error) {
    console.error('发货失败:', error);
    showFailToast('发货失败');
  }
};

const handleComplete = async () => {
  try {
    await showConfirmDialog({
      title: '确认送达',
      message: '确认该订单已送达吗？',
    });
    
    const result = await completeOrder(orderDetail.value.id);
    if (result && result.code === 200) {
      showSuccessToast('订单已完成');
      loadOrderDetail();
    } else {
      showFailToast(result.msg || '操作失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      showFailToast('操作失败');
    }
  }
};

const handleAddLogistics = () => {
  currentLogistics.value = null;
  logisticsForm.value = {
    shippingCompany: '顺丰速运',
    trackingNo: '',
    remark: ''
  };
  showLogisticsDialog.value = true;
};

const handleEditLogistics = (item) => {
  currentLogistics.value = item;
  logisticsForm.value = {
    shippingCompany: item.shippingCompany,
    trackingNo: item.trackingNo,
    remark: item.remark || ''
  };
  showLogisticsDialog.value = true;
};

const confirmLogistics = async () => {
  if (!logisticsForm.value.trackingNo.trim()) {
    showToast('请输入物流单号');
    return;
  }

  try {
    let result;
    if (currentLogistics.value) {
      // 编辑
      result = await updateLogistics(currentLogistics.value.id, {
        orderId: orderDetail.value.id,
        ...logisticsForm.value
      });
    } else {
      // 添加
      result = await addLogistics({
        orderId: orderDetail.value.id,
        ...logisticsForm.value
      });
    }

    if (result && result.code === 200) {
      showSuccessToast(currentLogistics.value ? '更新成功' : '添加成功');
      showLogisticsDialog.value = false;
      loadLogisticsList();
    } else {
      showFailToast(result.msg || '操作失败');
    }
  } catch (error) {
    showFailToast('操作失败');
  }
};

const handleDeleteLogistics = async (logisticsId) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '确定要删除该物流信息吗？',
    });
    
    const result = await deleteLogistics(logisticsId);
    if (result && result.code === 200) {
      showSuccessToast('删除成功');
      loadLogisticsList();
    } else {
      showFailToast(result.msg || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      showFailToast('删除失败');
    }
  }
};

const goBack = () => {
  router.back();
};

const goToLogistics = () => {
  router.push(`/merchant/logistics/${orderDetail.value.id}`);
};

const getStatusText = (status) => {
  if (!status) return '';
  const statusMap = {
    'PENDING': '待付款',
    'PAID': '已付款',
    'SHIPPED': '已发货',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  };
  return statusMap[status] || status;
};

const getStatusClass = (status) => {
  if (!status) return '';
  const classMap = {
    'PENDING': 'status-pending',
    'PAID': 'status-paid',
    'SHIPPED': 'status-shipped',
    'COMPLETED': 'status-completed',
    'CANCELLED': 'status-cancelled'
  };
  return classMap[status] || '';
};

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN');
};

onMounted(() => {
  loadOrderDetail();
});
</script>

<style scoped>
.order-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f0f0;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #2d3436;
  margin: 0;
}

.status-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-paid {
  background: #d1ecf1;
  color: #0c5460;
}

.status-shipped {
  background: #cce5ff;
  color: #004085;
}

.status-completed {
  background: #d4edda;
  color: #155724;
}

.status-cancelled {
  background: #f8d7da;
  color: #721c24;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.info-row .label {
  width: 120px;
  color: #636e72;
  flex-shrink: 0;
}

.info-row .value {
  color: #2d3436;
  flex: 1;
}

.total-row {
  margin-top: 8px;
  padding-top: 12px;
  border-top: 1px dashed #e0e0e0;
  font-size: 16px;
  font-weight: 600;
}

.price-highlight {
  color: #98D8C8;
  font-size: 18px;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.goods-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.goods-info {
  flex: 1;
}

.goods-name {
  font-size: 14px;
  color: #2d3436;
  font-weight: 500;
  margin-bottom: 4px;
}

.goods-spec {
  font-size: 12px;
  color: #95a5a6;
}

.goods-price {
  font-size: 14px;
  color: #98D8C8;
  font-weight: 600;
  width: 80px;
  text-align: right;
}

.goods-quantity {
  font-size: 14px;
  color: #636e72;
  width: 60px;
  text-align: center;
}

.goods-total {
  font-size: 15px;
  color: #2d3436;
  font-weight: 600;
  width: 100px;
  text-align: right;
}

.remark-content {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 14px;
  color: #636e72;
  line-height: 1.6;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

/* 对话框样式 */
.dialog {
  padding: 24px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.dialog-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3436;
  margin: 0;
}

.close-btn {
  width: 32px;
  height: 32px;
  background: #f8f9fa;
  border: none;
  border-radius: 8px;
  font-size: 24px;
  color: #636e72;
  cursor: pointer;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #e9ecef;
}

.dialog-body {
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #2d3436;
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #98D8C8;
  box-shadow: 0 0 0 3px rgba(152, 216, 200, 0.1);
}

.form-group input:disabled {
  background: #f8f9fa;
  color: #95a5a6;
}

.form-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
  background: white;
  cursor: pointer;
}

.form-select:focus {
  border-color: #98D8C8;
  box-shadow: 0 0 0 3px rgba(152, 216, 200, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel,
.btn-confirm {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background: #f8f9fa;
  color: #636e72;
}

.btn-cancel:hover {
  background: #e9ecef;
}

.btn-confirm {
  background: linear-gradient(135deg, #98D8C8 0%, #6BCF9F 100%);
  color: white;
}

.btn-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(152, 216, 200, 0.3);
}

.logistics-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.logistics-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  gap: 16px;
}

.logistics-info {
  flex: 1;
}

.logistics-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 8px;
  font-size: 14px;
}

.logistics-row:last-child {
  margin-bottom: 0;
}

.logistics-label {
  width: 80px;
  color: #636e72;
  flex-shrink: 0;
}

.logistics-value {
  color: #2d3436;
  flex: 1;
  word-break: break-all;
}

.logistics-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.empty-logistics {
  padding: 40px 20px;
  text-align: center;
  color: #95a5a6;
}

.empty-logistics p {
  margin: 0;
  font-size: 14px;
}
</style>
