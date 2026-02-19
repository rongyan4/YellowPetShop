<template>
  <div class="address-view">
    <!-- 顶部导航栏 -->
    <div class="nav-bar">
      <div class="nav-left" @click="goBack">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" fill="none"/>
        </svg>
      </div>
      <div class="nav-title">收货地址</div>
      <div class="nav-right"></div>
    </div>

    <!-- 地址列表 -->
    <div class="address-list">
      <div 
        v-for="(address, index) in addressList" 
        :key="address.id"
        class="address-item"
        @click="selectAddress(address)"
      >
        <div class="address-content">
          <div class="address-header">
            <span class="receiver-name">{{ address.receiverName }}</span>
            <span class="receiver-phone">{{ address.receiverPhone }}</span>
            <span v-if="address.isDefault" class="default-tag">默认</span>
          </div>
          <div class="address-detail">
            {{ address.province }} {{ address.city }} {{ address.district }} {{ address.detailAddress }}
          </div>
        </div>
        <div class="address-actions">
          <div class="action-btn" @click.stop="editAddress(address)">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" fill="currentColor"/>
            </svg>
          </div>
          <div class="action-btn" @click.stop="deleteAddress(address, index)">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" fill="currentColor"/>
            </svg>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="addressList.length === 0" class="empty-state">
        <svg viewBox="0 0 64 64" width="80" height="80">
          <circle cx="32" cy="32" r="30" fill="#f5f5f5"/>
          <path d="M32 20v24M20 32h24" stroke="#ccc" stroke-width="3" stroke-linecap="round"/>
        </svg>
        <p class="empty-text">暂无收货地址</p>
      </div>
    </div>

    <!-- 添加地址按钮 -->
    <div class="add-address-btn" @click="addAddress">
      <svg viewBox="0 0 24 24" width="20" height="20">
        <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
      </svg>
      <span>添加新地址</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { showToast, showConfirmDialog } from 'vant';

const router = useRouter();

// 地址列表
const addressList = ref([
  {
    id: 1,
    receiverName: '张三',
    receiverPhone: '138****5678',
    province: '广东省',
    city: '深圳市',
    district: '南山区',
    detailAddress: '科技园南区深圳湾科技生态园10栋A座',
    isDefault: true
  },
  {
    id: 2,
    receiverName: '李四',
    receiverPhone: '139****1234',
    province: '广东省',
    city: '广州市',
    district: '天河区',
    detailAddress: '珠江新城花城大道88号',
    isDefault: false
  }
]);

// 返回上一页
const goBack = () => {
  router.back();
};

// 选择地址
const selectAddress = (address) => {
  console.log('选择地址:', address);
  // TODO: 实现地址选择逻辑
};

// 添加地址
const addAddress = () => {
  console.log('添加新地址');
  showToast('添加地址功能开发中');
  // TODO: 跳转到添加地址页面
};

// 编辑地址
const editAddress = (address) => {
  console.log('编辑地址:', address);
  showToast('编辑地址功能开发中');
  // TODO: 跳转到编辑地址页面
};

// 删除地址
const deleteAddress = async (address, index) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '确定要删除这个地址吗？',
    });
    
    addressList.value.splice(index, 1);
    showToast('删除成功');
    // TODO: 调用删除地址API
  } catch {
    // 用户取消删除
  }
};

onMounted(() => {
  // TODO: 获取地址列表
  console.log('加载地址列表');
});
</script>

<style scoped>
.address-view {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

/* 顶部导航栏 */
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 56px;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  z-index: 100;
  border-bottom: 1px solid #f0f0f0;
}

.nav-left,
.nav-right {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #333;
}

.nav-title {
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

/* 地址列表 */
.address-list {
  margin-top: 56px;
  padding: 12px 16px;
}

.address-item {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.2s;
}

.address-item:active {
  transform: scale(0.98);
  background-color: #f8f8f8;
}

.address-content {
  flex: 1;
  min-width: 0;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.receiver-phone {
  font-size: 14px;
  color: #666;
}

.default-tag {
  padding: 2px 8px;
  background-color: #ff4d4f;
  color: #fff;
  font-size: 12px;
  border-radius: 4px;
}

.address-detail {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  word-break: break-all;
}

.address-actions {
  display: flex;
  gap: 16px;
  margin-left: 16px;
}

.action-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:active {
  transform: scale(0.9);
  color: #333;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.empty-text {
  margin-top: 16px;
  font-size: 14px;
  color: #999;
}

/* 添加地址按钮 */
.add-address-btn {
  position: fixed;
  bottom: 20px;
  left: 16px;
  right: 16px;
  height: 50px;
  background-color: #000;
  color: #fff;
  border-radius: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.2s;
}

.add-address-btn:active {
  transform: scale(0.98);
  background-color: #333;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .address-item {
    padding: 12px;
  }
  
  .receiver-name {
    font-size: 15px;
  }
  
  .address-detail {
    font-size: 13px;
  }
}
</style>
