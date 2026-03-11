<template>
  <div class="logistics-management">
    <div class="page-header">
      <h2>物流管理</h2>
      <p>订单号：{{ orderSn }}</p>
    </div>

    <div class="logistics-list">
      <div class="logistics-card" v-for="item in logisticsList" :key="item.id">
        <div class="card-header">
          <div class="status-badge" :class="getStatusClass(item.status)">
            {{ getStatusText(item.status) }}
          </div>
          <div class="card-actions">
            <button @click="handleEdit(item)" class="btn-edit">编辑</button>
            <button @click="handleDelete(item.id)" class="btn-delete">删除</button>
          </div>
        </div>
        
        <div class="card-body">
          <div class="info-row">
            <span class="label">物流公司：</span>
            <span class="value">{{ item.shippingCompany }}</span>
          </div>
          <div class="info-row">
            <span class="label">物流单号：</span>
            <span class="value">{{ item.trackingNo }}</span>
          </div>
          <div class="info-row" v-if="item.remark">
            <span class="label">备注：</span>
            <span class="value">{{ item.remark }}</span>
          </div>
          <div class="info-row">
            <span class="label">发货时间：</span>
            <span class="value">{{ formatDate(item.shippingTime) }}</span>
          </div>
          <div class="info-row" v-if="item.deliveryTime">
            <span class="label">送达时间：</span>
            <span class="value">{{ formatDate(item.deliveryTime) }}</span>
          </div>
        </div>
      </div>

      <div class="empty-state" v-if="logisticsList.length === 0">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="M20 7h-9"></path>
          <path d="M14 17H5"></path>
          <circle cx="17" cy="17" r="3"></circle>
          <circle cx="7" cy="7" r="3"></circle>
        </svg>
        <p>暂无物流信息</p>
      </div>
    </div>

    <div class="action-bar">
      <van-button type="primary" @click="handleAdd">添加物流</van-button>
      <van-button @click="goBack">返回</van-button>
    </div>

    <!-- 物流表单对话框 -->
    <van-popup v-model:show="showDialog" position="center" :style="{ width: '90%', maxWidth: '500px', borderRadius: '16px' }">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ currentItem ? '编辑物流信息' : '添加物流信息' }}</h3>
          <button @click="showDialog = false" class="close-btn">×</button>
        </div>
        
        <div class="dialog-body">
          <div class="form-group">
            <label>物流公司 <span class="required">*</span></label>
            <select v-model="form.shippingCompany" class="form-select">
              <option value="顺丰速运">顺丰速运</option>
              <option value="中通快递">中通快递</option>
              <option value="圆通速递">圆通速递</option>
              <option value="申通快递">申通快递</option>
              <option value="韵达快递">韵达快递</option>
              <option value="邮政EMS">邮政EMS</option>
              <option value="京东物流">京东物流</option>
              <option value="德邦物流">德邦物流</option>
              <option value="天天快递">天天快递</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>物流单号 <span class="required">*</span></label>
            <input v-model="form.trackingNo" type="text" placeholder="请输入物流单号" />
          </div>
          
          <div class="form-group">
            <label>备注</label>
            <textarea v-model="form.remark" placeholder="请输入备注信息（可选）" rows="3"></textarea>
          </div>
        </div>
        
        <div class="dialog-footer">
          <button @click="showDialog = false" class="btn-cancel">取消</button>
          <button @click="handleSubmit" class="btn-confirm">确定</button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getOrderLogistics, addLogistics, updateLogistics, deleteLogistics, getOrderDetail } from '@/api/merchant';
import { showSuccessToast, showFailToast, showToast, showConfirmDialog } from 'vant';

const router = useRouter();
const route = useRoute();

const orderId = ref(null);
const orderSn = ref('');
const logisticsList = ref([]);
const showDialog = ref(false);
const currentItem = ref(null);

const form = ref({
  shippingCompany: '顺丰速运',
  trackingNo: '',
  remark: ''
});

const loadOrderInfo = async () => {
  try {
    const result = await getOrderDetail(orderId.value);
    if (result && result.code === 200) {
      orderSn.value = result.data.orderSn;
    }
  } catch (error) {
    console.error('加载订单信息失败:', error);
  }
};

const loadLogisticsList = async () => {
  try {
    const result = await getOrderLogistics(orderId.value);
    if (result && result.code === 200) {
      logisticsList.value = result.data || [];
    } else {
      showFailToast('加载物流信息失败');
    }
  } catch (error) {
    console.error('加载物流信息失败:', error);
    showFailToast('加载失败');
  }
};

const handleAdd = () => {
  currentItem.value = null;
  form.value = {
    shippingCompany: '顺丰速运',
    trackingNo: '',
    remark: ''
  };
  showDialog.value = true;
};

const handleEdit = (item) => {
  currentItem.value = item;
  form.value = {
    shippingCompany: item.shippingCompany,
    trackingNo: item.trackingNo,
    remark: item.remark || ''
  };
  showDialog.value = true;
};

const handleSubmit = async () => {
  if (!form.value.trackingNo.trim()) {
    showToast('请输入物流单号');
    return;
  }

  try {
    let result;
    if (currentItem.value) {
      result = await updateLogistics(currentItem.value.id, {
        orderId: orderId.value,
        ...form.value
      });
    } else {
      result = await addLogistics({
        orderId: orderId.value,
        ...form.value
      });
    }

    if (result && result.code === 200) {
      showSuccessToast(currentItem.value ? '更新成功' : '添加成功');
      showDialog.value = false;
      loadLogisticsList();
    } else {
      showFailToast(result.msg || '操作失败');
    }
  } catch (error) {
    showFailToast('操作失败');
  }
};

const handleDelete = async (logisticsId) => {
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

const getStatusText = (status) => {
  const statusMap = {
    shipped: '已发货',
    delivered: '已送达'
  };
  return statusMap[status] || status;
};

const getStatusClass = (status) => {
  const classMap = {
    shipped: 'status-shipped',
    delivered: 'status-delivered'
  };
  return classMap[status] || '';
};

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN');
};

onMounted(() => {
  orderId.value = route.params.id;
  if (orderId.value) {
    loadOrderInfo();
    loadLogisticsList();
  }
});
</script>

<style scoped>
.logistics-management {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #2d3436;
  margin: 0 0 8px 0;
}

.page-header p {
  font-size: 14px;
  color: #636e72;
  margin: 0;
}

.logistics-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.logistics-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.status-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;
}

.status-shipped {
  background: #cce5ff;
  color: #004085;
}

.status-delivered {
  background: #d4edda;
  color: #155724;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.btn-edit,
.btn-delete {
  padding: 6px 16px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-edit:hover {
  background: #bbdefb;
}

.btn-delete {
  background: #ffebee;
  color: #c62828;
}

.btn-delete:hover {
  background: #ffcdd2;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  font-size: 14px;
}

.info-row .label {
  width: 100px;
  color: #636e72;
  flex-shrink: 0;
}

.info-row .value {
  color: #2d3436;
  flex: 1;
  word-break: break-all;
}

.empty-state {
  padding: 80px 20px;
  text-align: center;
  color: #95a5a6;
  background: white;
  border-radius: 12px;
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

.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
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

.required {
  color: #ff6b6b;
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
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #98D8C8;
  box-shadow: 0 0 0 3px rgba(152, 216, 200, 0.1);
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
