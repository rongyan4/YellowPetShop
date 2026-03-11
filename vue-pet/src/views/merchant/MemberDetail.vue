<template>
  <div class="member-detail">
    <!-- 头部 -->
    <div class="header">
      <button @click="goBack" class="back-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        返回
      </button>
      <h1 class="title">会员详情</h1>
    </div>

    <div v-if="memberInfo" class="content">
      <!-- 基本信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h2>基本信息</h2>
          <button @click="openEditDialog" class="edit-btn">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
            编辑
          </button>
        </div>
        <div class="user-profile">
          <div class="avatar-section">
            <img :src="memberInfo.avatar || defaultAvatar" alt="用户头像" class="user-avatar" />
            <div class="avatar-info">
              <h3>{{ memberInfo.nickname || memberInfo.username }}</h3>
              <p class="user-id">ID: {{ memberInfo.id }}</p>
            </div>
          </div>
        </div>
        <div class="info-grid">
          <div class="info-item">
            <label>用户名</label>
            <span>{{ memberInfo.username }}</span>
          </div>
          <div class="info-item">
            <label>昵称</label>
            <span>{{ memberInfo.nickname || '-' }}</span>
          </div>
          <div class="info-item">
            <label>邮箱</label>
            <span>{{ memberInfo.email || '-' }}</span>
          </div>
          <div class="info-item">
            <label>手机号</label>
            <span>{{ memberInfo.phone || '-' }}</span>
          </div>
          <div class="info-item">
            <label>性别</label>
            <span>{{ getGenderText(memberInfo.gender) }}</span>
          </div>
          <div class="info-item">
            <label>生日</label>
            <span>{{ memberInfo.birthday || '-' }}</span>
          </div>
          <div class="info-item">
            <label>注册时间</label>
            <span>{{ formatDateTime(memberInfo.createTime) }}</span>
          </div>
          <div class="info-item">
            <label>账户状态</label>
            <span :class="['status-badge', memberInfo.status === 'active' ? 'active' : 'inactive']">
              {{ memberInfo.status === 'active' ? '正常' : '禁用' }}
            </span>
          </div>
        </div>
      </div>

      <!-- 钱包信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h2>钱包信息</h2>
          <button @click="showWalletDialog = true" class="edit-btn">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <rect x="1" y="4" width="22" height="16" rx="2" ry="2"></rect>
              <line x1="1" y1="10" x2="23" y2="10"></line>
            </svg>
            调整余额
          </button>
        </div>
        <div class="wallet-info">
          <div class="wallet-item">
            <div class="wallet-label">账户余额</div>
            <div class="wallet-value balance">¥{{ (memberInfo.balance || 0).toFixed(2) }}</div>
          </div>
          <div class="wallet-item">
            <div class="wallet-label">支付密码</div>
            <div class="wallet-value">
              {{ memberInfo.hasPayPassword ? '已设置' : '未设置' }}
              <button @click="showResetPasswordDialog = true" class="reset-btn">重置</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 收货地址卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h2>收货地址</h2>
          <button @click="showAddAddressDialog" class="edit-btn">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <line x1="12" y1="5" x2="12" y2="19"></line>
              <line x1="5" y1="12" x2="19" y2="12"></line>
            </svg>
            添加地址
          </button>
        </div>
        <div class="address-list">
          <div v-for="address in addressList" :key="address.id" class="address-item">
            <div class="address-content">
              <div class="address-header">
                <span class="receiver">{{ address.receiverName }}</span>
                <span class="phone">{{ address.receiverPhone }}</span>
                <span v-if="address.isDefault" class="default-badge">默认</span>
              </div>
              <div class="address-detail">
                {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}
              </div>
            </div>
            <div class="address-actions">
              <button @click="editAddress(address)" class="btn-edit">编辑</button>
              <button @click="deleteAddress(address.id)" class="btn-delete">删除</button>
            </div>
          </div>
          <div v-if="addressList.length === 0" class="empty-state">
            <p>暂无收货地址</p>
          </div>
        </div>
      </div>

      <!-- 订单统计卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h2>订单统计</h2>
        </div>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-value">{{ orderStats.totalOrders || 0 }}</div>
            <div class="stat-label">总订单数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">¥{{ (orderStats.totalAmount || 0).toFixed(2) }}</div>
            <div class="stat-label">总消费金额</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ orderStats.pendingOrders || 0 }}</div>
            <div class="stat-label">待付款</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ orderStats.completedOrders || 0 }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </div>
      </div>

      <!-- 订单列表 -->
      <div class="info-card">
        <div class="card-header">
          <h2>最近订单</h2>
        </div>
        <div class="order-list">
          <table class="order-table">
            <thead>
              <tr>
                <th>订单号</th>
                <th>商品</th>
                <th>金额</th>
                <th>状态</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orderList" :key="order.id">
                <td>{{ order.orderSn }}</td>
                <td>
                  <div class="order-goods">
                    {{ order.items && order.items.length > 0 ? order.items[0].commodityName : '-' }}
                    <span v-if="order.items && order.items.length > 1" class="more-goods">
                      等{{ order.items.length }}件商品
                    </span>
                  </div>
                </td>
                <td class="amount">¥{{ order.payAmount }}</td>
                <td>
                  <span class="order-status" :class="getOrderStatusClass(order.status)">
                    {{ order.statusText }}
                  </span>
                </td>
                <td>{{ formatDateTime(order.createTime) }}</td>
                <td>
                  <button @click="viewOrderDetail(order.id)" class="btn-view">查看</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="orderList.length === 0" class="empty-state">
            <p>暂无订单记录</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑基本信息对话框 -->
    <van-popup v-model:show="showEditDialog" position="center" :style="{ width: '90%', maxWidth: '500px', borderRadius: '16px' }">
      <div class="dialog">
        <div class="dialog-header">
          <h3>编辑基本信息</h3>
          <button @click="showEditDialog = false" class="close-btn">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>昵称</label>
            <input v-model="editForm.nickname" type="text" placeholder="请输入昵称" />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="editForm.email" type="email" placeholder="请输入邮箱" />
          </div>
          <div class="form-group">
            <label>手机号</label>
            <input v-model="editForm.phone" type="tel" placeholder="请输入手机号" />
          </div>
          <div class="form-group">
            <label>性别</label>
            <select v-model="editForm.gender" class="form-select">
              <option value="0">未知</option>
              <option value="1">男</option>
              <option value="2">女</option>
            </select>
          </div>
          <div class="form-group">
            <label>生日</label>
            <input v-model="editForm.birthday" type="date" />
          </div>
        </div>
        <div class="dialog-footer">
          <button @click="showEditDialog = false" class="btn-cancel">取消</button>
          <button @click="handleUpdateInfo" class="btn-confirm">保存</button>
        </div>
      </div>
    </van-popup>

    <!-- 添加/编辑地址对话框 -->
    <van-popup v-model:show="showAddressDialog" position="center" :style="{ width: '90%', maxWidth: '500px', borderRadius: '16px' }">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ addressForm.id ? '编辑地址' : '添加地址' }}</h3>
          <button @click="showAddressDialog = false" class="close-btn">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>收货人</label>
            <input v-model="addressForm.receiverName" type="text" placeholder="请输入收货人姓名" />
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input v-model="addressForm.receiverPhone" type="tel" placeholder="请输入联系电话" />
          </div>
          <div class="form-group">
            <label>省份</label>
            <input v-model="addressForm.province" type="text" placeholder="请输入省份" />
          </div>
          <div class="form-group">
            <label>城市</label>
            <input v-model="addressForm.city" type="text" placeholder="请输入城市" />
          </div>
          <div class="form-group">
            <label>区/县</label>
            <input v-model="addressForm.district" type="text" placeholder="请输入区/县" />
          </div>
          <div class="form-group">
            <label>详细地址</label>
            <textarea v-model="addressForm.detailAddress" placeholder="请输入详细地址" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label class="checkbox-label">
              <input v-model="addressForm.isDefault" type="checkbox" />
              设为默认地址
            </label>
          </div>
        </div>
        <div class="dialog-footer">
          <button @click="showAddressDialog = false" class="btn-cancel">取消</button>
          <button @click="handleSaveAddress" class="btn-confirm">保存</button>
        </div>
      </div>
    </van-popup>

    <!-- 钱包管理对话框 -->
    <van-popup v-model:show="showWalletDialog" position="center" :style="{ width: '90%', maxWidth: '500px', borderRadius: '16px' }">
      <div class="dialog">
        <div class="dialog-header">
          <h3>钱包管理</h3>
          <button @click="showWalletDialog = false" class="close-btn">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>当前余额</label>
            <div class="balance-display">¥{{ memberInfo ? (memberInfo.balance || 0).toFixed(2) : '0.00' }}</div>
          </div>
          <div class="form-group">
            <label>调整金额</label>
            <input v-model="walletForm.amount" type="number" step="0.01" placeholder="输入正数增加，负数减少" />
          </div>
          <div class="form-group">
            <label>备注说明</label>
            <textarea v-model="walletForm.remark" placeholder="请输入调整原因" rows="3"></textarea>
          </div>
        </div>
        <div class="dialog-footer">
          <button @click="showWalletDialog = false" class="btn-cancel">取消</button>
          <button @click="handleAdjustBalance" class="btn-confirm">确定</button>
        </div>
      </div>
    </van-popup>

    <!-- 重置支付密码对话框 -->
    <van-popup v-model:show="showResetPasswordDialog" position="center" :style="{ width: '90%', maxWidth: '500px', borderRadius: '16px' }">
      <div class="dialog">
        <div class="dialog-header">
          <h3>重置支付密码</h3>
          <button @click="showResetPasswordDialog = false" class="close-btn">×</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>新支付密码（6位数字）</label>
            <input v-model="passwordForm.newPassword" type="password" maxlength="6" placeholder="请输入6位数字密码" />
          </div>
          <div class="form-group">
            <label>确认密码</label>
            <input v-model="passwordForm.confirmPassword" type="password" maxlength="6" placeholder="请再次输入密码" />
          </div>
        </div>
        <div class="dialog-footer">
          <button @click="showResetPasswordDialog = false" class="btn-cancel">取消</button>
          <button @click="handleResetPayPassword" class="btn-confirm">确定</button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { 
  getMemberDetail, 
  updateMemberInfo, 
  adjustMemberBalance, 
  resetMemberPayPassword,
  getMemberOrders,
  getMemberAddressList,
  addMemberAddress,
  updateMemberAddress,
  deleteMemberAddress
} from '@/api/merchantMember';
import { showSuccessToast, showFailToast, showConfirmDialog } from 'vant';

const router = useRouter();
const route = useRoute();

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

const memberInfo = ref(null);
const addressList = ref([]);
const orderStats = ref({});
const orderList = ref([]);

const showEditDialog = ref(false);
const showAddressDialog = ref(false);
const showWalletDialog = ref(false);
const showResetPasswordDialog = ref(false);

const editForm = ref({
  nickname: '',
  email: '',
  phone: '',
  gender: '0',
  birthday: ''
});

const addressForm = ref({
  id: null,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
});

const walletForm = ref({
  amount: '',
  remark: ''
});

const passwordForm = ref({
  newPassword: '',
  confirmPassword: ''
});

// 加载会员详情
const loadMemberDetail = async () => {
  const userId = route.params.id;
  if (!userId) {
    showFailToast('会员ID不存在');
    goBack();
    return;
  }

  try {
    const response = await getMemberDetail(userId);
    if (response && response.code === 200) {
      memberInfo.value = response.data;
      
      // 初始化编辑表单
      editForm.value = {
        nickname: memberInfo.value.nickname || '',
        email: memberInfo.value.email || '',
        phone: memberInfo.value.phone || '',
        gender: memberInfo.value.gender || '0',
        birthday: memberInfo.value.birthday || ''
      };
    } else {
      showFailToast(response.msg || '加载失败');
    }
  } catch (error) {
    console.error('加载会员详情失败:', error);
    showFailToast('加载失败');
  }
};

// 加载会员地址
const loadMemberAddresses = async () => {
  const userId = route.params.id;
  try {
    const response = await getMemberAddressList(userId);
    if (response && response.code === 200) {
      addressList.value = response.data || [];
    }
  } catch (error) {
    console.error('加载地址列表失败:', error);
  }
};

// 加载会员订单
const loadMemberOrders = async () => {
  const userId = route.params.id;
  try {
    const response = await getMemberOrders(userId, { page: 1, pageSize: 10 });
    if (response && response.code === 200) {
      orderList.value = response.data.list || [];
      calculateOrderStats();
    }
  } catch (error) {
    console.error('加载订单列表失败:', error);
  }
};

// 计算订单统计
const calculateOrderStats = () => {
  if (!orderList.value || orderList.value.length === 0) {
    orderStats.value = {
      totalOrders: 0,
      totalAmount: 0,
      pendingOrders: 0,
      completedOrders: 0
    };
    return;
  }

  const stats = {
    totalOrders: orderList.value.length,
    totalAmount: 0,
    pendingOrders: 0,
    completedOrders: 0
  };

  orderList.value.forEach(order => {
    stats.totalAmount += parseFloat(order.payAmount || 0);
    if (order.status === 'PENDING') {
      stats.pendingOrders++;
    } else if (order.status === 'COMPLETED') {
      stats.completedOrders++;
    }
  });

  orderStats.value = stats;
};

// 打开编辑对话框
const openEditDialog = () => {
  editForm.value = {
    nickname: memberInfo.value.nickname || '',
    email: memberInfo.value.email || '',
    phone: memberInfo.value.phone || '',
    gender: memberInfo.value.gender || '0',
    birthday: memberInfo.value.birthday || ''
  };
  showEditDialog.value = true;
};

// 更新基本信息
const handleUpdateInfo = async () => {
  const userId = route.params.id;
  try {
    const response = await updateMemberInfo(userId, editForm.value);
    if (response && response.code === 200) {
      showSuccessToast('更新成功');
      showEditDialog.value = false;
      loadMemberDetail();
    } else {
      showFailToast(response.msg || '更新失败');
    }
  } catch (error) {
    showFailToast('更新失败');
  }
};

// 显示添加地址对话框
const showAddAddressDialog = () => {
  addressForm.value = {
    id: null,
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false
  };
  showAddressDialog.value = true;
};

// 编辑地址
const editAddress = (address) => {
  addressForm.value = {
    id: address.id,
    receiverName: address.receiverName,
    receiverPhone: address.receiverPhone,
    province: address.province,
    city: address.city,
    district: address.district,
    detailAddress: address.detailAddress,
    isDefault: address.isDefault || false
  };
  showAddressDialog.value = true;
};

// 保存地址
const handleSaveAddress = async () => {
  // 验证表单
  if (!addressForm.value.receiverName) {
    showFailToast('请输入收货人姓名');
    return;
  }
  if (!addressForm.value.receiverPhone) {
    showFailToast('请输入联系电话');
    return;
  }
  if (!addressForm.value.province || !addressForm.value.city || !addressForm.value.district) {
    showFailToast('请完整填写省市区信息');
    return;
  }
  if (!addressForm.value.detailAddress) {
    showFailToast('请输入详细地址');
    return;
  }

  const userId = route.params.id;
  try {
    let response;
    if (addressForm.value.id) {
      // 更新地址
      response = await updateMemberAddress(addressForm.value.id, addressForm.value);
    } else {
      // 添加地址
      response = await addMemberAddress(userId, addressForm.value);
    }
    
    if (response && response.code === 200) {
      showSuccessToast(addressForm.value.id ? '更新成功' : '添加成功');
      showAddressDialog.value = false;
      loadMemberAddresses();
    } else {
      showFailToast(response.msg || '操作失败');
    }
  } catch (error) {
    showFailToast('操作失败');
  }
};

// 删除地址
const deleteAddress = async (addressId) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '确定要删除这个地址吗？'
    });
    
    const response = await deleteMemberAddress(addressId);
    if (response && response.code === 200) {
      showSuccessToast('删除成功');
      loadMemberAddresses();
    } else {
      showFailToast(response.msg || '删除失败');
    }
  } catch (error) {
    // 用户取消删除
    if (error !== 'cancel') {
      showFailToast('删除失败');
    }
  }
};

// 调整余额
const handleAdjustBalance = async () => {
  if (!walletForm.value.amount) {
    showFailToast('请输入调整金额');
    return;
  }
  
  const userId = route.params.id;
  try {
    const response = await adjustMemberBalance(userId, walletForm.value);
    if (response && response.code === 200) {
      showSuccessToast('调整成功');
      showWalletDialog.value = false;
      walletForm.value = { amount: '', remark: '' };
      loadMemberDetail();
    } else {
      showFailToast(response.msg || '调整失败');
    }
  } catch (error) {
    showFailToast('调整失败');
  }
};

// 重置支付密码
const handleResetPayPassword = async () => {
  if (!passwordForm.value.newPassword || passwordForm.value.newPassword.length !== 6) {
    showFailToast('请输入6位数字密码');
    return;
  }
  
  if (!/^\d{6}$/.test(passwordForm.value.newPassword)) {
    showFailToast('密码必须是6位数字');
    return;
  }
  
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    showFailToast('两次密码输入不一致');
    return;
  }
  
  const userId = route.params.id;
  try {
    const response = await resetMemberPayPassword(userId, {
      newPassword: passwordForm.value.newPassword
    });
    if (response && response.code === 200) {
      showSuccessToast('重置成功');
      showResetPasswordDialog.value = false;
      passwordForm.value = { newPassword: '', confirmPassword: '' };
      loadMemberDetail();
    } else {
      showFailToast(response.msg || '重置失败');
    }
  } catch (error) {
    showFailToast('重置失败');
  }
};

// 查看订单详情
const viewOrderDetail = (orderId) => {
  router.push(`/merchant/orders/${orderId}`);
};

// 返回
const goBack = () => {
  router.back();
};

// 工具函数
const getGenderText = (gender) => {
  if (gender === 1) return '男';
  if (gender === 2) return '女';
  return '未知';
};

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN');
};

const getOrderStatusClass = (status) => {
  const classMap = {
    'PENDING': 'pending',
    'PAID': 'paid',
    'SHIPPED': 'shipped',
    'COMPLETED': 'completed',
    'CANCELLED': 'cancelled'
  };
  return classMap[status] || '';
};

onMounted(() => {
  loadMemberDetail();
  loadMemberAddresses();
  loadMemberOrders();
});
</script>

<style scoped>
.member-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  color: #636e72;
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover {
  border-color: #98D8C8;
  color: #98D8C8;
}

.back-btn svg {
  width: 18px;
  height: 18px;
  stroke-width: 2;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #2d3436;
  margin: 0;
}

.content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #2d3436;
  margin: 0;
}

.edit-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  background: #e3f2fd;
  color: #1976d2;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-btn:hover {
  background: #bbdefb;
}

.edit-btn svg {
  width: 16px;
  height: 16px;
  stroke-width: 2;
}

.user-profile {
  margin-bottom: 24px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 12px;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-info h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3436;
  margin: 0 0 8px 0;
}

.user-id {
  font-size: 14px;
  color: #636e72;
  margin: 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item label {
  font-size: 13px;
  color: #95a5a6;
  font-weight: 500;
}

.info-item span {
  font-size: 15px;
  color: #2d3436;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.status-badge.inactive {
  background: #f8d7da;
  color: #721c24;
}

.wallet-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.wallet-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.wallet-label {
  font-size: 13px;
  color: #95a5a6;
  font-weight: 500;
}

.wallet-value {
  font-size: 15px;
  color: #2d3436;
  display: flex;
  align-items: center;
  gap: 12px;
}

.wallet-value.balance {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #98D8C8 0%, #6BCF9F 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.reset-btn {
  padding: 4px 12px;
  background: #fff3cd;
  color: #856404;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.reset-btn:hover {
  background: #ffeaa7;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  transition: all 0.2s;
}

.address-item:hover {
  border-color: #98D8C8;
  box-shadow: 0 2px 8px rgba(152, 216, 200, 0.2);
}

.address-content {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.receiver {
  font-size: 15px;
  font-weight: 600;
  color: #2d3436;
}

.phone {
  font-size: 14px;
  color: #636e72;
}

.default-badge {
  padding: 2px 8px;
  background: #98D8C8;
  color: white;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.address-detail {
  font-size: 14px;
  color: #636e72;
  line-height: 1.6;
}

.address-actions {
  display: flex;
  gap: 8px;
}

.btn-edit,
.btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 12px;
  transition: all 0.3s;
}

.stat-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #98D8C8 0%, #6BCF9F 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 13px;
  color: #636e72;
  font-weight: 500;
}

.order-table {
  width: 100%;
  border-collapse: collapse;
}

.order-table thead {
  background: #f8f9fa;
}

.order-table th {
  padding: 12px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #2d3436;
}

.order-table td {
  padding: 12px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #636e72;
}

.order-goods {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.more-goods {
  font-size: 12px;
  color: #95a5a6;
}

.amount {
  color: #98D8C8;
  font-weight: 600;
}

.order-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.order-status.pending {
  background: #fff3cd;
  color: #856404;
}

.order-status.paid {
  background: #cce5ff;
  color: #004085;
}

.order-status.shipped {
  background: #e7e3ff;
  color: #5a32a3;
}

.order-status.completed {
  background: #d4edda;
  color: #155724;
}

.order-status.cancelled {
  background: #f8d7da;
  color: #721c24;
}

.btn-view {
  padding: 4px 12px;
  background: #e3f2fd;
  color: #1976d2;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-view:hover {
  background: #bbdefb;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: #95a5a6;
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
  font-size: 18px;
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
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.close-btn:hover {
  background: #e9ecef;
}

.dialog-body {
  margin-bottom: 24px;
  max-height: 60vh;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #2d3436;
  margin-bottom: 8px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: auto;
  cursor: pointer;
}

.form-group input,
.form-group textarea,
.form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus,
.form-select:focus {
  border-color: #98D8C8;
  box-shadow: 0 0 0 3px rgba(152, 216, 200, 0.1);
}

.balance-display {
  font-size: 24px;
  font-weight: 600;
  color: #98D8C8;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  text-align: center;
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
