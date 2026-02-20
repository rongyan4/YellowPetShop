<template>
  <div class="order-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="search-box">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <circle cx="11" cy="11" r="8"></circle>
          <path d="m21 21-4.35-4.35"></path>
        </svg>
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索订单号..."
          class="search-input"
          @keyup.enter="handleSearch"
        />
      </div>
      <select v-model="statusFilter" @change="handleSearch" class="status-select">
        <option value="">全部状态</option>
        <option value="pending">待付款</option>
        <option value="paid">已付款</option>
        <option value="shipped">已发货</option>
        <option value="completed">已完成</option>
        <option value="cancelled">已取消</option>
      </select>
    </div>

    <!-- 订单列表 -->
    <div class="order-table-wrapper">
      <table class="order-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>用户ID</th>
            <th>订单金额</th>
            <th>订单状态</th>
            <th>物流状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in orderList" :key="item.id">
            <td class="order-no">{{ item.orderNo }}</td>
            <td>{{ item.userId }}</td>
            <td class="price">¥{{ item.totalAmount }}</td>
            <td>
              <span class="status-badge" :class="getStatusClass(item.status)">
                {{ getStatusText(item.status) }}
              </span>
            </td>
            <td>{{ getShippingStatusText(item.shippingStatus) }}</td>
            <td>{{ formatDate(item.createTime) }}</td>
            <td>
              <div class="action-buttons">
                <button @click="handleViewDetail(item)" class="btn-view">查看</button>
                <button 
                  v-if="item.status === 'pending'" 
                  @click="handleUpdatePrice(item)" 
                  class="btn-edit"
                >
                  改价
                </button>
                <button 
                  v-if="item.status === 'paid'" 
                  @click="handleShip(item)" 
                  class="btn-ship"
                >
                  发货
                </button>
                <button 
                  v-if="item.status !== 'completed' && item.status !== 'cancelled'" 
                  @click="handleCancel(item)" 
                  class="btn-cancel"
                >
                  取消
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="orderList.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <p>暂无订单数据</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <button @click="handlePageChange(currentPage - 1)" :disabled="currentPage === 1" class="page-btn">
        上一页
      </button>
      <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
      <button @click="handlePageChange(currentPage + 1)" :disabled="currentPage === totalPages" class="page-btn">
        下一页
      </button>
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
            <input type="text" :value="'¥' + currentOrder?.totalAmount" disabled />
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getOrderList, updateOrderPrice, shipOrder } from '@/api/merchant';
import { showConfirmDialog, showSuccessToast, showFailToast, showToast } from 'vant';

const router = useRouter();

const searchKeyword = ref('');
const statusFilter = ref('');
const orderList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const totalPages = ref(0);

const showPriceDialog = ref(false);
const showShipDialog = ref(false);
const currentOrder = ref(null);
const newTotalAmount = ref('');
const newPostage = ref('');
const priceReason = ref('');
const trackingNo = ref('');
const shippingCompany = ref('顺丰速运');
const shipRemark = ref('');

const loadOrderList = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    };
    
    if (statusFilter.value) {
      params.status = statusFilter.value;
    }
    
    const result = await getOrderList(params);

    if (result && result.code === 200) {
      orderList.value = result.data.records || [];
      total.value = result.data.total || 0;
      totalPages.value = result.data.pages || 0;
    } else {
      showFailToast(result.msg || '加载失败');
    }
  } catch (error) {
    console.error('加载订单列表失败:', error);
    showFailToast('加载失败');
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadOrderList();
};

const handlePageChange = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    loadOrderList();
  }
};

const handleViewDetail = (item) => {
  router.push(`/merchant/orders/${item.id}`);
};

const handleUpdatePrice = (item) => {
  currentOrder.value = item;
  newTotalAmount.value = item.totalAmount;
  newPostage.value = item.postage || 0;
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
      orderId: currentOrder.value.id,
      newTotalAmount: parseFloat(newTotalAmount.value),
      newPostage: parseFloat(newPostage.value),
      reason: priceReason.value
    });

    if (result && result.code === 200) {
      showSuccessToast('价格修改成功');
      showPriceDialog.value = false;
      loadOrderList();
    } else {
      showFailToast(result.msg || '修改失败');
    }
  } catch (error) {
    showFailToast('修改失败');
  }
};

const handleShip = (item) => {
  currentOrder.value = item;
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
    const result = await shipOrder({
      orderId: currentOrder.value.id,
      shippingCompany: shippingCompany.value,
      trackingNo: trackingNo.value,
      remark: shipRemark.value
    });

    if (result && result.code === 200) {
      showSuccessToast('发货成功');
      showShipDialog.value = false;
      loadOrderList();
    } else {
      showFailToast(result.msg || '发货失败');
    }
  } catch (error) {
    showFailToast('发货失败');
  }
};

const handleCancel = (item) => {
  showConfirmDialog({
    title: '确认取消订单',
    message: '确定要取消该订单吗？',
    confirmButtonColor: '#ff6b6b'
  }).then(async () => {
    showToast('取消订单功能待实现');
  }).catch(() => {});
};

const getStatusText = (status) => {
  const statusMap = {
    pending: '待付款',
    paid: '已付款',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消'
  };
  return statusMap[status] || status;
};

const getStatusClass = (status) => {
  const classMap = {
    pending: 'status-pending',
    paid: 'status-paid',
    shipped: 'status-shipped',
    completed: 'status-completed',
    cancelled: 'status-cancelled'
  };
  return classMap[status] || '';
};

const getShippingStatusText = (status) => {
  if (!status) return '未发货';
  const statusMap = {
    pending: '待发货',
    shipped: '已发货',
    delivered: '已送达'
  };
  return statusMap[status] || status;
};

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN');
};

onMounted(() => {
  loadOrderList();
});
</script>

<style scoped>
.order-management {
  max-width: 1400px;
}

.action-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.search-box {
  flex: 1;
  max-width: 400px;
  position: relative;
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  stroke-width: 2;
  color: #95a5a6;
}

.search-input {
  width: 100%;
  height: 44px;
  padding: 0 16px 0 48px;
  border: 1px solid #e8e8e8;
  border-radius: 10px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.search-input:focus {
  border-color: #98D8C8;
  box-shadow: 0 0 0 3px rgba(152, 216, 200, 0.1);
}

.status-select {
  height: 44px;
  padding: 0 16px;
  border: 1px solid #e8e8e8;
  border-radius: 10px;
  font-size: 14px;
  outline: none;
  cursor: pointer;
  transition: all 0.2s;
}

.status-select:focus {
  border-color: #98D8C8;
}

.order-table-wrapper {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.order-table {
  width: 100%;
  border-collapse: collapse;
}

.order-table thead {
  background: #f8f9fa;
}

.order-table th {
  padding: 16px;
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  color: #2d3436;
}

.order-table td {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #636e72;
}

.order-no {
  font-family: 'Courier New', monospace;
  color: #2d3436;
  font-weight: 500;
}

.price {
  color: #98D8C8;
  font-weight: 600;
  font-size: 15px;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
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

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn-view,
.btn-edit,
.btn-ship,
.btn-cancel {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.btn-view {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-view:hover {
  background: #bbdefb;
}

.btn-edit {
  background: #fff3cd;
  color: #856404;
}

.btn-edit:hover {
  background: #ffeaa7;
}

.btn-ship {
  background: #d4edda;
  color: #155724;
}

.btn-ship:hover {
  background: #c3e6cb;
}

.btn-cancel {
  background: #ffebee;
  color: #c62828;
}

.btn-cancel:hover {
  background: #ffcdd2;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #95a5a6;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  stroke-width: 1.5;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
}

.page-btn {
  padding: 8px 20px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  border-color: #98D8C8;
  color: #98D8C8;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #636e72;
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

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.form-group input:focus {
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
</style>
